package io.github.kartoffelsup.nuntius.ui.message

import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.api.user.result.UserContact
import io.github.kartoffelsup.nuntius.ui.components.CenteredRow
import io.github.kartoffelsup.nuntius.ui.user.SubmitButton
import io.github.kartoffelsup.nuntius.ui.user.SubmitButtonState

sealed class Message {
    abstract val text: String
}

data class UserMessage(override val text: String) : Message()
data class ConversationPartnerMessage(override val text: String) : Message()

@Model
class MessageFieldState(
    var initial: Boolean = true,
    var value: TextFieldValue = TextFieldValue("")
)

@Model
class MessageScreenState(
    var currentConversationPartner: UserContact? = null,
    val messages: List<Message> = listOf(),
    val messageFieldState: MessageFieldState = MessageFieldState()
)

@Composable
fun MessageScreen(state: MessageScreenState) {
    Column {
        Text(text = "Chatting with: ${state.currentConversationPartner?.username}")
    }
    Column(
        modifier = Modifier.fillMaxSize() + Modifier.padding(5.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column {
            state.messages.forEach {
                val background: Color
                val textColor: Color
                val arrangement: Arrangement.Horizontal
                if (it is UserMessage) {
                    background = Color(0xFF82B1FF)
                    arrangement = Arrangement.End
                    textColor = Color(0xFFE0F7FA)
                } else {
                    background = Color(0xFFC8E6C9)
                    arrangement = Arrangement.Start
                    textColor = Color(0xFFBF360C)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = arrangement
                ) {
                    TextMessageView(
                        text = it.text,
                        bgColor = background,
                        textColor = textColor
                    )
                }
            }
        }
        Divider(
            thickness = 1.5.dp,
            color = Color(0xFF7C4DFF),
            modifier = Modifier.padding(5.dp)
        )
        CenteredRow(
            modifier = Modifier.gravity(Alignment.CenterHorizontally)
        ) {
            TextField(
                modifier = Modifier.padding(5.dp) + Modifier.weight(85f),
                value = if (state.messageFieldState.initial) {
                    TextFieldValue("Enter message here...")
                } else {
                    state.messageFieldState.value
                },
                textStyle = currentTextStyle(),
                onFocus = {
                    state.messageFieldState.initial = false
                },
                onValueChange = { text -> state.messageFieldState.value = text }
            )
            SubmitButton(
                state = SubmitButtonState(
                    enabled = state.messageFieldState.value.text.isNotEmpty()
                ),
                modifier = Modifier.weight(15f)
            ) {
                Icon(
                    asset = vectorResource(R.drawable.ic_outline_navigation_24),
                    modifier = Modifier.preferredSizeIn(minWidth = 16.dp, minHeight = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun MessageScreenPreview() {
    MessageScreen(
        MessageScreenState(
            UserContact("id", "Michelle"),
            listOf(
                UserMessage("Hi"),
                ConversationPartnerMessage("Hi"),
                UserMessage("Wie geht's?"),
                ConversationPartnerMessage("Gut, danke der Nachfrage. Und dir? :)")
            )
        )
    )
}
