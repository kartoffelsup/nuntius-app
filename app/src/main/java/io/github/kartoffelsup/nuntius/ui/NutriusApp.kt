package io.github.kartoffelsup.nuntius.ui

import androidx.annotation.StringRes
import androidx.compose.animation.core.DefaultAnimationClock
import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.ui.tooling.preview.Preview
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.api.user.result.UserContact
import io.github.kartoffelsup.nuntius.api.user.result.UserContacts
import io.github.kartoffelsup.nuntius.data.user.UserData
import io.github.kartoffelsup.nuntius.ui.components.AppDrawer
import io.github.kartoffelsup.nuntius.ui.components.NuntiusDrawerState
import io.github.kartoffelsup.nuntius.ui.home.HomeScreen
import io.github.kartoffelsup.nuntius.ui.message.MessageScreen
import io.github.kartoffelsup.nuntius.ui.message.MessageScreenState
import io.github.kartoffelsup.nuntius.ui.user.UserLoginScreen

class AppState @OptIn(ExperimentalMaterialApi::class) constructor(
    userData: UserData? = null,
    scaffoldState: ScaffoldState = ScaffoldState(
        drawerState = DrawerState(
            DrawerValue.Closed,
            DefaultAnimationClock()
        ), SnackbarHostState()
    ),
    appDrawerState: NuntiusDrawerState = NuntiusDrawerState(),
    messageScreenState: MessageScreenState = MessageScreenState(),
    @StringRes
    titleResource: Int = R.string.app_name
) {
    var userData by mutableStateOf(userData)
    var scaffoldState by mutableStateOf(scaffoldState)
    var appDrawerState by mutableStateOf(appDrawerState)
    var messageScreenState by mutableStateOf(messageScreenState)
    var titleResource by mutableStateOf(titleResource)
}

@Composable
fun NutriusApp(appState: AppState, navigationViewModel: NavigationViewModel) {
    MaterialTheme(colors = lightThemeColors) {
        Column {
            Scaffold(
                scaffoldState = appState.scaffoldState,
                drawerContent = {
                    AppDrawer(
                        currentScreen = navigationViewModel.currentScreen,
                        appState = appState,
                        closeDrawer = {
                            appState.scaffoldState.drawerState.close()
                        },
                        navigationViewModel = navigationViewModel
                    )
                },
                topBar = {
                    TopAppBar(
                        modifier = Modifier.preferredHeightIn(maxHeight = 32.dp),
                        backgroundColor = MaterialTheme.colors.primaryVariant
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(Modifier.clickable(onClick = {
                                appState.scaffoldState.drawerState.open()
                            }), children = {
                                Icon(
                                    asset = vectorResource(R.drawable.ic_outline_menu_24),
                                    modifier = Modifier.fillMaxHeight().then(
                                        Modifier.wrapContentSize(
                                            Alignment.CenterStart
                                        )
                                    ),
                                    tint = Color.White
                                )
                            })
                            Text(
                                text = stringResource(appState.titleResource),
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                                    .gravity(Alignment.CenterVertically)
                            )
                        }
                    }
                },
                bodyContent = { innerPadding ->
                    AppContent(
                        appState,
                        innerPadding,
                        navigationViewModel
                    )
                })
        }
    }
}

@Composable
private fun AppContent(
    appState: AppState,
    innerPadding: InnerPadding,
    navigationViewModel: NavigationViewModel
) {
    when (navigationViewModel.currentScreen) {
        is Screen.Home -> HomeScreen(appState, innerPadding)
        is Screen.Login -> UserLoginScreen(appState, innerPadding, navigationViewModel)
        is Screen.Messages -> MessageScreen(appState.messageScreenState)
    }
}

@Preview
@Composable
fun NutriusAppPreview() {
    val state by remember {
        mutableStateOf(
            AppState(
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
            )
        )
    }
    NutriusApp(
        appState = state, navigationViewModel = NavigationViewModel(SavedStateHandle())
    )
}
