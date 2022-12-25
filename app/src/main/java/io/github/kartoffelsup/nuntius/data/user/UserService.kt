package io.github.kartoffelsup.nuntius.data.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.github.kartoffelsup.nuntius.api.user.request.CreateUserRequest
import io.github.kartoffelsup.nuntius.api.user.request.LoginRequest
import io.github.kartoffelsup.nuntius.api.user.request.UpdateNotificationTokenRequest
import io.github.kartoffelsup.nuntius.api.user.result.*
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

    suspend fun signup(username: String, email: String, password: String): Either<String, CreateUserResult> {
        val request = CreateUserRequest(username, email, password)
        val apiResult = nuntiusApiService.post(
            "user",
            request,
            CreateUserRequest.serializer(),
            CreateUserResult.serializer()
        )

        return when (apiResult) {
            is Success<*> -> {
                (apiResult.payload as CreateUserResult).right()
            }
            is Failure -> apiResult.reason.left()
        }
    }

    suspend fun login(email: String, password: String): LoginResult {
        val request = LoginRequest(email, password)
        val apiResult = nuntiusApiService.post(
            "user/login",
            request,
            LoginRequest.serializer(),
            SuccessfulLogin.serializer()
        )

        return when (apiResult) {
            is Success<*> -> {
                val successfulLogin = apiResult.payload as SuccessfulLogin
                successfulLogin.also { login ->
                    val userData = loadContacts(login)
                    bus.post(Login(userData))
                }
            }
            is Failure -> FailedLogin(apiResult.reason)
        }
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
        return when (result) {
            is Success<*> -> (result.payload as String).right()
            is Failure -> result.reason.left()
        }
    }

    private suspend fun loadContacts(successfulLogin: SuccessfulLogin): UserData {
        val contacts = when (val contactsResult = getContacts(successfulLogin.token)) {
            is Success<*> -> contactsResult.payload as UserContacts

            else -> UserContacts(listOf())
        }
        return UserData(
            successfulLogin.token,
            successfulLogin.userId,
            successfulLogin.username,
            contacts
        )
    }

    private suspend fun getContacts(credentials: String): ApiResult {
        return nuntiusApiService.get(
            "user/contacts",
            UserContacts.serializer(),
            credentials = credentials
        )
    }
}
