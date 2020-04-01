package io.github.kartoffelsup.nuntius.ui

import androidx.annotation.StringRes
import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.api.user.result.UserContact
import io.github.kartoffelsup.nuntius.api.user.result.UserContacts
import io.github.kartoffelsup.nuntius.data.user.UserData
import io.github.kartoffelsup.nuntius.ui.components.AppDrawer
import io.github.kartoffelsup.nuntius.ui.components.NuntiusDrawerState
import io.github.kartoffelsup.nuntius.ui.home.HomeScreen
import io.github.kartoffelsup.nuntius.ui.home.MessageHolder
import io.github.kartoffelsup.nuntius.ui.message.MessageScreen
import io.github.kartoffelsup.nuntius.ui.message.MessageScreenState
import io.github.kartoffelsup.nuntius.ui.user.UserLoginScreen

@Model
class AppState(
    var userData: UserData? = null,
    val scaffoldState: ScaffoldState = ScaffoldState(drawerState = DrawerState.Closed),
    var appDrawerState: NuntiusDrawerState = NuntiusDrawerState(),
    var messageScreenState: MessageScreenState = MessageScreenState(),
    @StringRes
    var titleResource: Int = R.string.app_name
)

@Composable
fun NutriusApp(appState: AppState) {
    MaterialTheme(colors = lightThemeColors) {
        Column {
            Scaffold(
                scaffoldState = appState.scaffoldState,
                drawerContent = {
                    AppDrawer(
                        currentScreen = NutriusAppStatus.currentScreen,
                        appState = appState,
                        closeDrawer = {
                            appState.scaffoldState.drawerState = DrawerState.Closed
                        }
                    )
                },
                topAppBar = {
                    TopAppBar(
                        modifier = Modifier.preferredHeightIn(maxHeight = 32.dp),
                        color = MaterialTheme.colors.primaryVariant
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), arrangement = Arrangement.Center) {
                            Clickable(onClick = {
                                appState.scaffoldState.drawerState = DrawerState.Opened
                            }) {
                                Icon(
                                    asset = vectorResource(R.drawable.ic_outline_menu_24),
                                    modifier = Modifier.fillMaxHeight() + Modifier.wrapContentSize(
                                        Alignment.CenterStart
                                    ),
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = stringResource(appState.titleResource),
                                modifier = Modifier.wrapContentSize(Alignment.Center) + Modifier.gravity(
                                    RowAlign.Center
                                )
                            )
                        }
                    }
                }, bodyContent = { modifier -> AppContent(appState, modifier) })
        }
    }
}

@Composable
private fun AppContent(appState: AppState, modifier: Modifier) {
    when (NutriusAppStatus.currentScreen) {
        is Screen.Home -> HomeScreen(appState, modifier, MessageHolder(""))
        is Screen.Login -> UserLoginScreen(appState, modifier)
        is Screen.Messages -> MessageScreen(appState.messageScreenState)
    }
}

@Preview
@Composable
fun NutriusAppPreview() {
    NutriusApp(
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
