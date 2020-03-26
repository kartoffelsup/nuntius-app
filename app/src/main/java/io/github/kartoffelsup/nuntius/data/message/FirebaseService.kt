package io.github.kartoffelsup.nuntius.data.message

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.kartoffelsup.nuntius.data.Security
import io.github.kartoffelsup.nuntius.data.user.UserService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class FirebaseService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.i("NOTIFICATION_TOKEN", "New token: $newToken")
        Security.getUser()?.let { user ->
            GlobalScope.launch {
                UserService.updateToken(newToken, user)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("REMOTE_MSG", "${remoteMessage.data}")
        EventBus.getDefault().post(remoteMessage.data["userMessage"]!!)
    }
}
