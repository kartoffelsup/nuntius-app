package io.github.kartoffelsup.nuntius.data.message

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.kartoffelsup.nuntius.api.notification.NuntiusNotificationDto
import io.github.kartoffelsup.nuntius.data.Security
import io.github.kartoffelsup.nuntius.data.jsonx
import io.github.kartoffelsup.nuntius.data.user.UserService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class FirebaseService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Security.getUser()?.let { user ->
            GlobalScope.launch {
                UserService.updateToken(newToken, user)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val payload = remoteMessage.data["data"]
        val notification = payload?.let {
            jsonx.parse(NuntiusNotificationDto.serializer(), it)
        }
        EventBus.getDefault().post(notification)
    }
}
