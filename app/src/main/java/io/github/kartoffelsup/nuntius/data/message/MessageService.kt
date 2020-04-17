package io.github.kartoffelsup.nuntius.data.message

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.github.kartoffelsup.nuntius.api.message.request.SendMessageRequest
import io.github.kartoffelsup.nuntius.api.message.result.SendMessageResult
import io.github.kartoffelsup.nuntius.client.ApiResult
import io.github.kartoffelsup.nuntius.client.Failure
import io.github.kartoffelsup.nuntius.client.Success
import io.github.kartoffelsup.nuntius.data.nuntiusApiService
import io.github.kartoffelsup.nuntius.data.user.UserData

object MessageService {
    suspend fun send(
        recipientId: String,
        message: String,
        credentials: UserData
    ): Either<String, SendMessageResult> {
        val post: ApiResult = nuntiusApiService.post(
            path = "message",
            request = SendMessageRequest(
                text = message,
                recipient = recipientId,
                sendTimestamp = "2020-03-21T00:00:00.000Z"
            ),
            requestSerializer = SendMessageRequest.serializer(),
            responseSerializer = SendMessageResult.serializer(),
            credentials = credentials.token
        )
        return when(post) {
            is Success<*> -> (post.payload as SendMessageResult).right()
            is Failure -> post.reason.left()
        }
    }
}
