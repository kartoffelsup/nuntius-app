package io.github.kartoffelsup.nuntius.ui.user

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeightIn
import androidx.ui.layout.preferredWidthIn
import androidx.ui.material.Surface
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.api.user.result.UserContact
import io.github.kartoffelsup.nuntius.api.user.result.UserContacts
import io.github.kartoffelsup.nuntius.data.user.UserData
import io.github.kartoffelsup.nuntius.ui.AppState
import io.github.kartoffelsup.nuntius.ui.Screen
import io.github.kartoffelsup.nuntius.ui.home.ContactState
import io.github.kartoffelsup.nuntius.ui.navigateTo

@Composable
fun ContactsView(appState: AppState) {
    if (appState.userData?.contacts?.contacts?.isNotEmpty() == true) {
        Column {
            VerticalScroller(
                modifier = Modifier.padding(5.dp) + Modifier.preferredWidthIn(minWidth = 120.dp) + Modifier.preferredHeightIn(
                    maxHeight = 200.dp
                )
            ) {
                Column {
                    appState.userData?.contacts?.contacts?.forEach { contact ->
                        val contactState = ContactState(true)
                        Column {
                            Clickable(onClick = {
                                contactState.open = !contactState.open
                            }) {
                                UserRow(
                                    username = contact.username
                                )
                            }
                            if (contactState.open) {
                                Surface(
                                    color = Color(0xFFCCFF90),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column {
                                        Clickable(
                                            onClick = {
                                                appState.messageScreenState.currentConversationPartner =
                                                    contact
                                                navigateTo(Screen.Messages)
                                            }
                                        ) {
                                            Text(
                                                modifier = Modifier.padding(2.dp),
                                                text = stringResource(
                                                    R.string.message_contact,
                                                    contact.username
                                                )
                                            )
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

@Preview
@Composable
fun ContactsViewPreview() {
    ContactsView(
        appState = AppState(
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
        )
    )
}