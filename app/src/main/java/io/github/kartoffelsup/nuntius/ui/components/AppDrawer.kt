package io.github.kartoffelsup.nuntius.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.TextButton
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
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

    val surfaceModifier = modifier +
            Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp) +
            Modifier.fillMaxWidth()
    Surface(
        modifier = surfaceModifier,
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        TextButton(onClick = action, modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    modifier = Modifier.gravity(Alignment.CenterVertically)
                        .preferredSize(24.dp),
                    asset = vectorResource(icon),
                    tint = textIconColor
                )
                Spacer(Modifier.preferredWidth(16.dp))
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
