package io.github.kartoffelsup.nuntius.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.data.user.UserService
import io.github.kartoffelsup.nuntius.ui.AppState
import io.github.kartoffelsup.nuntius.ui.NavigationViewModel
import io.github.kartoffelsup.nuntius.ui.Screen
import io.github.kartoffelsup.nuntius.ui.user.ContactsView

class NuntiusDrawerState(
    displayingContacts: Boolean = false
) {
    var displayingContacts by mutableStateOf(displayingContacts)
}

@Composable
fun AppDrawer(
    currentScreen: Screen,
    appState: AppState,
    closeDrawer: () -> Unit,
    navigationViewModel: NavigationViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        DrawerButton(
            icon = R.drawable.ic_outline_home_24,
            label = "Home",
            isSelected = currentScreen == Screen.Home
        ) {
            navigationViewModel.navigateTo(Screen.Home)
            closeDrawer()
        }

        if (appState.userData == null) {
            DrawerButton(
                icon = R.drawable.ic_outline_person_outline_24,
                label = stringResource(R.string.common_signin_button_text),
                isSelected = currentScreen == Screen.Login
            ) {
                navigationViewModel.navigateTo(Screen.Login)
                closeDrawer()
            }
        } else {
            DrawerButton(
                icon = R.drawable.ic_outline_email_24,
                label = stringResource(R.string.messages_label),
                isSelected = currentScreen == Screen.Messages
            ) {
                navigationViewModel.navigateTo(Screen.Messages)
                closeDrawer()
            }
            DrawerButton(
                icon = R.drawable.ic_outline_contacts_24,
                label = stringResource(R.string.contacts),
                isSelected = appState.appDrawerState.displayingContacts
            ) {
                appState.appDrawerState.displayingContacts = !appState.appDrawerState.displayingContacts
            }
            if (appState.appDrawerState.displayingContacts) {
                ContactsView(appState = appState, navigationViewModel = navigationViewModel)
            }
            DrawerButton(
                icon = R.drawable.ic_outline_remove_circle_outline_24,
                label = stringResource(R.string.logout_button_text),
                isSelected = currentScreen == Screen.Login
            ) {
                UserService.logout()
                navigationViewModel.navigateTo(Screen.Login)
                closeDrawer()
            }
        }
    }
}

@Composable
private fun DrawerButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    label: String,
    isSelected: Boolean,
    action: () -> Unit
) {
    val colors = MaterialTheme.colors
    val textIconColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        colors.surface
    }

    val surfaceModifier =
        modifier.then(Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp))
            .then(Modifier.fillMaxWidth())
    Surface(
        modifier = surfaceModifier,
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        TextButton(onClick = action, modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(24.dp),
                    painter = painterResource(icon),
                    contentDescription = "icon",
                    tint = textIconColor
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    style = (MaterialTheme.typography).body2.copy(
                        color = textIconColor
                    )
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun AppdrawerPreview() {
    AppDrawer(
        currentScreen = Screen.Home,
        closeDrawer = {},
        appState = AppState(),
        navigationViewModel = NavigationViewModel(SavedStateHandle())
    )
}
