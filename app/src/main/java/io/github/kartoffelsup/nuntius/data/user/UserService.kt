package io.github.kartoffelsup.nuntius.data.user

import arrow.core.*
import arrow.core.extensions.fx
import arrow.fx.IO
import io.github.kartoffelsup.nuntius.api.user.request.LoginRequest
import io.github.kartoffelsup.nuntius.api.user.request.UpdateNotificationTokenRequest
import io.github.kartoffelsup.nuntius.api.user.result.FailedLogin
import io.github.kartoffelsup.nuntius.api.user.result.LoginResult
import io.github.kartoffelsup.nuntius.api.user.result.SuccessfulLogin
import io.github.kartoffelsup.nuntius.api.user.result.UserContacts
import io.github.kartoffelsup.nuntius.client.ApiResult
import io.github.kartoffelsup.nuntius.client.Failure
import io.github.kartoffelsup.nuntius.client.Success
import io.github.kartoffelsup.nuntius.data.Login
import io.github.kartoffelsup.nuntius.data.Logout
import io.github.kartoffelsup.nuntius.data.nuntiusApiService
import kotlinx.serialization.builtins.serializer
import org.greenrobot.eventbus.EventBus

object UserService {

    private val bus: EventBus = EventBus.getDefault()

    suspend fun login(email: String, password: String): LoginResult {
        val request = LoginRequest(email, password)
        val apiResult = nuntiusApiService.post(
            "user/login",
            request,
            LoginRequest.serializer(),
            SuccessfulLogin.serializer()
        )

        val loginResult: LoginResult = when (apiResult) {
            is Success<*> -> apiResult.payload as SuccessfulLogin
            is Failure -> FailedLogin(apiResult.reason)
        }

        when (loginResult) {
            is SuccessfulLogin -> {
                when (val contactsResult = getContacts(loginResult.token)) {
                    is Success<*> -> {
                        val contacts = contactsResult.payload as UserContacts
                        val user = UserData(
                            loginResult.token,
                            loginResult.userId,
                            loginResult.username,
                            contacts
                        )
                        bus.post(Login(user))
                    }
                }
            }
        }
        return loginResult
    }

    fun logout() {
        bus.post(Logout)
    }

    suspend fun updateToken(token: String, credentials: UserData): Either<String, String> {
        val result = nuntiusApiService.post(
            "user/notification-token",
            UpdateNotificationTokenRequest(token),
            UpdateNotificationTokenRequest.serializer(),
            String.serializer(),
            credentials = credentials.token
        )
        return when(result) {
            is Success<*> -> (result.payload as String).right()
            is Failure -> result.reason.left()
        }
    }

    private suspend fun getContacts(credentials: String): ApiResult {
        return nuntiusApiService.get(
            "user/contacts",
            UserContacts.serializer(),
            credentials = credentials
        )
    }
}
