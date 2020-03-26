package io.github.kartoffelsup.nuntius.ui.home

import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.*
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.api.user.result.UserContact
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

@Model
class MessageHolder(var message: String)

@Composable
fun HomeScreen(appState: AppState, modifier: Modifier, messageHolder: MessageHolder) {
    val user = appState.userData
    Column(modifier = modifier) {
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
            Spacer(LayoutHeight(16.dp))
            if (user != null) {
                CenteredRow {
                    Button(modifier = LayoutPadding(start = 5.dp, end = 5.dp), onClick = {
                        GlobalScope.launch {
                            val result = MessageService.send(user.userId, "Hello There!", user)
                            withContext(Dispatchers.Main) {
                                messageHolder.message = result.fold({ it }, { it.messageId })
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
            Column(LayoutSize.Fill + LayoutAlign.Center) {
                Text(
                    modifier = LayoutWidth.Fill + LayoutAlign.Center,
                    text = "User",
                    style = currentTextStyle()
                        .copy(fontSize = 24.sp, textAlign = TextAlign.Center)
                )
                CenteredRow {
                    UserRow(username = user.username)
                }

                if (user.contacts.contacts.isNotEmpty()) {
                    Column {
                        Text(
                            modifier = LayoutWidth.Fill,
                            text = "Contacts",
                            style = currentTextStyle()
                                .copy(fontSize = 24.sp, textAlign = TextAlign.Center)
                        )
                        VerticalScroller(
                            modifier = LayoutWidth.Fill + LayoutAlign.Center + LayoutHeight.Max(
                                100.dp
                            )
                        ) {
                            Column {
                                user.contacts.contacts.forEach {
                                    val contactState = ContactState(false)
                                    Column {
                                        Clickable(onClick = {
                                            contactState.open = !contactState.open
                                        }) {
                                            UserRow(
                                                username = it.username
                                            )
                                        }
                                        if (contactState.open) {
                                            DropdownPopup {
                                                Surface(color = Color.DarkGray) {
                                                    Column() {
                                                        Text("Hello")
                                                        Text("Bye")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Model
class ContactState(var open: Boolean)

@Preview
@Composable
fun HomePreview() {
    MaterialTheme {
        HomeScreen(
            AppState(
                userData = UserData(
                    "",
                    "marvin",
                    "Marvin1",
                    UserContacts(
                        listOf(
                            UserContact("id", "Contact1"),
                            UserContact("id", "Contact2"),
                            UserContact("id", "Contact3"),
                            UserContact("id", "Contact4"),
                            UserContact("id", "Contact5"),
                            UserContact("id", "Contact1"),
                            UserContact("id", "Contact2"),
                            UserContact("id", "Contact3"),
                            UserContact("id", "Contact4"),
                            UserContact("id", "Contact5"),
                            UserContact("id", "Contact6")
                        )
                    )
                )
            ),
            Modifier.None,
            MessageHolder("hello there")
        )
    }
}
