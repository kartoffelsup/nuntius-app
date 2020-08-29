package io.github.kartoffelsup.nuntius.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.github.kartoffelsup.nuntius.api.user.result.UserContact
import io.github.kartoffelsup.nuntius.api.user.result.UserContacts
import io.github.kartoffelsup.nuntius.data.user.UserData
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json
import kotlinx.serialization.json.jsonArray
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

object Security {
    private const val USER_ALIAS: String = "NUTRIUS_USER"
    private const val ENCRYPTED_PREFS_FILE_NAME: String = "default_prefs"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        EventBus.getDefault().register(this)
        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_FILE_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun destroy() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 99)
    fun clearUser(event: Logout) {
        println(event)
        sharedPreferences.edit()
            .remove(USER_ALIAS)
            .apply()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 99)
    fun storeUser(event: Login) {
        val contacts = jsonArray {
            event.userData.contacts.contacts.map {
                +json {
                    "userId" to it.userId
                    "username" to it.username
                }
            }
        }
        val userJson: JsonObject = json {
            "token" to event.userData.token
            "userId" to event.userData.userId
            "username" to event.userData.username
            "contacts" to contacts
        }
        sharedPreferences.edit()
            .putString(
                USER_ALIAS,
                jsonx.stringify(JsonObject.serializer(), userJson)
            )
            .apply()
    }

    fun getUser(): UserData? {
        val storedData = sharedPreferences.getString(USER_ALIAS, null)
        return storedData?.let {
            jsonx.parseJson(it)
                .takeIf { json -> json is JsonObject }
                ?.let { json: JsonElement -> json.jsonObject }
                ?.let { json ->
                    val token = json["token"]?.primitive?.content
                    val userId = json["userId"]?.primitive?.content
                    val username = json["username"]?.primitive?.content
                    val contacts = json["contacts"]?.jsonArray?.let { cs ->
                        cs.content.map { elem ->
                            val id = elem.jsonObject.getPrimitive("userId").content
                            val name = elem.jsonObject.getPrimitive("username").content
                            UserContact(id, name)
                        }
                    } ?: emptyList()
                    if (token != null && userId != null && username != null) {
                        UserData(
                            token,
                            userId,
                            username,
                            UserContacts(contacts)
                        )
                    } else {
                        null
                    }
                }
        }
    }
}

class Login(val userData: UserData)
object Logout
