package io.github.kartoffelsup.nuntius.ui

import androidx.compose.Model

sealed class Screen {
    object Home : Screen()
    object Login : Screen()
    object Messages: Screen()
}

@Model
object NutriusAppStatus {
    var currentScreen: Screen = Screen.Home
}

fun navigateTo(screen: Screen) {
    NutriusAppStatus.currentScreen = screen
}
