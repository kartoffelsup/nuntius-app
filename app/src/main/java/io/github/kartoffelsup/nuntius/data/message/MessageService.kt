package io.github.kartoffelsup.nuntius.data.message

import arrow.core.Either
import io.github.kartoffelsup.nuntius.api.message.request.SendMessageRequest
import io.github.kartoffelsup.nuntius.api.message.result.SendMessageResult
import io.github.kartoffelsup.nuntius.data.NutriusApiService
import io.github.kartoffelsup.nuntius.data.user.UserData

object MessageService {
    suspend fun send(
        recipientId: String,
        message: String,
        credentials: UserData
    ): Either<String, SendMessageResult> {
        return NutriusApiService.post(
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
    }
}
