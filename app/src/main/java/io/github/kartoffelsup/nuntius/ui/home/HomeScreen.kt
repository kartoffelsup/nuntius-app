package io.github.kartoffelsup.nuntius.ui.home

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.currentTextStyle
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.api.user.result.UserContacts
import io.github.kartoffelsup.nuntius.data.message.MessageService
import io.github.kartoffelsup.nuntius.data.user.UserData
import io.github.kartoffelsup.nuntius.data.user.UserService
import io.github.kartoffelsup.nuntius.ui.AppState
import io.github.kartoffelsup.nuntius.ui.components.CenteredRow
import io.github.kartoffelsup.nuntius.ui.user.UserRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MessageHolder(val message: String)

@Composable
fun HomeScreen(appState: AppState, innerPadding: InnerPadding) {
    val user = appState.userData
    val messageHolder by state {MessageHolder("")}
    Column(modifier = Modifier.padding(innerPadding)) {
        Column {
            CenteredRow {
                Text(
                    text = "Welcome to nuntius",
                    style = currentTextStyle().copy(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
            Spacer(Modifier.preferredHeight(16.dp))
            if (user != null) {
                CenteredRow {
                    Button(modifier = Modifier.padding(start = 5.dp, end = 5.dp), onClick = {
                        GlobalScope.launch {
                            val result = MessageService.send(user.userId, "Hello There!", user)
                            withContext(Dispatchers.Main) {
                                messageHolder.copy(message = result.fold({ it }, { it.messageId }))
                            }
                        }
                    }) {
                        Text(text = "Send Test Message")
                    }
                    Button(onClick = { UserService.logout() }) {
                        Text(text = stringResource(R.string.logout_button_text))
                    }
                }
            }
            CenteredRow { Text(text = messageHolder.message) }
        }

        if (user != null) {
            Column(Modifier.fillMaxSize() + Modifier.wrapContentSize(Alignment.Center)) {
                Text(
                    modifier = Modifier.fillMaxWidth() + Modifier.wrapContentSize(Alignment.Center),
                    text = "User",
                    style = currentTextStyle()
                        .copy(fontSize = 24.sp, textAlign = TextAlign.Center)
                )
                CenteredRow {
                    UserRow(username = user.username)
                }
            }
        }
    }
}

data class ContactState(var open: Boolean)

@Preview
@Composable
fun HomePreview() {
    MaterialTheme {
        HomeScreen(
            AppState(
                userData = UserData(
                    "",
                    "nobody",
                    "Nobody1",
                    UserContacts(listOf())
                )
            ),
            InnerPadding()
        )
    }
}
