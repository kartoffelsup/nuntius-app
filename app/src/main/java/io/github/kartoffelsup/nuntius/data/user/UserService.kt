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
import io.github.kartoffelsup.nuntius.data.Login
import io.github.kartoffelsup.nuntius.data.Logout
import io.github.kartoffelsup.nuntius.data.NutriusApiService
import kotlinx.serialization.builtins.serializer
import org.greenrobot.eventbus.EventBus

object UserService {

    private val bus: EventBus = EventBus.getDefault()

    suspend fun login(email: String, password: String): LoginResult {
        val request = LoginRequest(email, password)
        val loginEither = NutriusApiService.post(
            "user/login",
            request,
            LoginRequest.serializer(),
            SuccessfulLogin.serializer()
        )

        val result = when (loginEither) {
            is Either.Right -> {
                val login = loginEither.b
                val contactsEither = getContacts(login.token)
                contactsEither.map { contacts ->
                    login toT UserData(
                        login.token,
                        login.userId,
                        login.username,
                        contacts
                    )
                }
            }
            is Either.Left -> loginEither
        }

        return result
            .map { bus.post(Login(it.b)); it.a }
            .getOrHandle { FailedLogin(it) }
    }

    fun logout() {
        bus.post(Logout)
    }

    suspend fun updateToken(token: String, credentials: UserData): Either<String, String> {
        val result = NutriusApiService.post(
            "user/notification-token",
            UpdateNotificationTokenRequest(token),
            UpdateNotificationTokenRequest.serializer(),
            String.serializer(),
            credentials = credentials.token
        )
        return result
    }

    private suspend fun getContacts(credentials: String): Either<String, UserContacts> {
        return NutriusApiService.get(
            "user/contacts",
            UserContacts.serializer(),
            credentials = credentials
        )
    }
}
