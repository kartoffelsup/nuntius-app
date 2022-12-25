package io.github.kartoffelsup.nuntius.ui.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.api.user.result.UserContact
import io.github.kartoffelsup.nuntius.api.user.result.UserContacts
import io.github.kartoffelsup.nuntius.data.user.UserData
import io.github.kartoffelsup.nuntius.ui.AppState
import io.github.kartoffelsup.nuntius.ui.NavigationViewModel
import io.github.kartoffelsup.nuntius.ui.Screen
import io.github.kartoffelsup.nuntius.ui.home.ContactState

@Composable
fun ContactsView(appState: AppState, navigationViewModel: NavigationViewModel) {
    if (appState.userData?.contacts?.contacts?.isNotEmpty() == true) {
        Column {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .sizeIn(minWidth = 120.dp, maxHeight = 200.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    appState.userData?.contacts?.contacts?.forEach { contact ->
                        var contactState by remember { mutableStateOf(ContactState(true)) }
                        Column(modifier = Modifier.padding(3.dp).padding(1.dp)) {
                            Box(Modifier.clickable(onClick = {
                                contactState = contactState.copy(open = !contactState.open)
                            })) {
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
                                        Box(Modifier.clickable(onClick = {
                                            appState.messageScreenState.currentConversationPartner =
                                                contact
                                            navigationViewModel.navigateTo(Screen.Messages)
                                        })) {
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
                "nobody",
                "Nobody1",
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
        ), navigationViewModel = NavigationViewModel(SavedStateHandle())
    )
}