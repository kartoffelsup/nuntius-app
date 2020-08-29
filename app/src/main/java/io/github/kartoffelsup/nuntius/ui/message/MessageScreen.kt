package io.github.kartoffelsup.nuntius.ui.message

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.currentTextStyle
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focusObserver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
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

class MessageFieldState(
    initial: Boolean = true,
    value: TextFieldValue = TextFieldValue("")
) {
   var initial by mutableStateOf(initial)
   var value by mutableStateOf(value)
}

class MessageScreenState(
    currentConversationPartner: UserContact? = null,
    messages: List<Message> = listOf(),
    messageFieldState: MessageFieldState = MessageFieldState()
) {
    var currentConversationPartner by mutableStateOf(currentConversationPartner)
    var messages by mutableStateOf(messages)
    var messageFieldState by mutableStateOf(messageFieldState)
}

@OptIn(InternalLayoutApi::class, ExperimentalFocus::class)
@Composable
fun MessageScreen(state: MessageScreenState) {
    Column {
        Text(text = "Chatting with: ${state.currentConversationPartner?.username}")
    }
    Column(
        modifier = Modifier.fillMaxSize().then(Modifier.padding(5.dp)),
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
                modifier = Modifier.padding(5.dp).weight(85f).focusObserver {
                    state.messageFieldState.initial = false
                },
                value = if (state.messageFieldState.initial) {
                    TextFieldValue("Enter message here...")
                } else {
                    state.messageFieldState.value
                },
                textStyle = currentTextStyle(),
                label = {},
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
            UserContact("id", "Nobody2"),
            listOf(
                UserMessage("Hi"),
                ConversationPartnerMessage("Hi"),
                UserMessage("Wie geht's?"),
                ConversationPartnerMessage("Gut, danke der Nachfrage. Und dir? :)")
            )
        )
    )
}
