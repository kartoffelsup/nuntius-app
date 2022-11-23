package io.github.kartoffelsup.nuntius.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.api.user.result.UserContacts
import io.github.kartoffelsup.nuntius.data.message.MessageService
import io.github.kartoffelsup.nuntius.data.user.UserData
import io.github.kartoffelsup.nuntius.data.user.UserService
import io.github.kartoffelsup.nuntius.ui.AppState
import io.github.kartoffelsup.nuntius.ui.components.CenteredRow
import io.github.kartoffelsup.nuntius.ui.user.UserRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MessageHolder(val message: String = "")

@Composable
fun HomeScreen(appState: AppState, innerPadding: PaddingValues) {
    val user = appState.userData
    var messageHolder by remember { mutableStateOf(MessageHolder()) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(innerPadding)) {
        Column {
            CenteredRow {
                Text(
                    text = "Welcome to nuntius",
                    style = TextStyle.Default.copy(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
            Spacer(Modifier.height(16.dp))
            if (user != null) {
                CenteredRow {
                    Button(modifier = Modifier.padding(start = 5.dp, end = 5.dp), onClick = {
                        coroutineScope.launch {
                            val result = withContext(Dispatchers.IO) {
                                MessageService.send(user.userId, "Hello There!", user)
                            }
                            withContext(Dispatchers.Main) {
                                messageHolder = messageHolder.copy(
                                    message = result.fold({ it },
                                        { it.messageId })
                                )
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
            Column(
                Modifier
                    .fillMaxSize()
                    .then(Modifier.wrapContentSize(Alignment.Center))
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(Modifier.wrapContentSize(Alignment.Center)),
                    text = "User",
                    style = TextStyle.Default
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
            PaddingValues()
        )
    }
}
