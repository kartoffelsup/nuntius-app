/**
 * https://github.com/android/compose-samples/blob/master/JetNews/app/src/main/java/com/example/jetnews/ui/Navigation.kt
 */
package io.github.kartoffelsup.nuntius.ui

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.compose.MutableState
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

sealed class Screen(val name: String) {
    object Home : Screen("Home")
    object Login : Screen("Login")
    object Messages : Screen("Messages")
}

private const val SIS_SCREEN = "sis_screen"
private const val SIS_NAME = "screen_name"

/**
 * Convert a screen to a bundle that can be stored in [SavedStateHandle]
 */
private fun Screen.toBundle(): Bundle {
    return bundleOf(SIS_NAME to name)
}

/**
 * Read a bundle stored by [Screen.toBundle] and return desired screen.
 *
 * @return the parsed [Screen]
 * @throws IllegalArgumentException if the bundle could not be parsed
 */
private fun Bundle.toScreen(): Screen {
    return when (getStringOrThrow(SIS_NAME)) {
        Screen.Home.name -> Screen.Home
        Screen.Login.name -> Screen.Login
        Screen.Messages.name -> Screen.Messages
        else -> Screen.Home
    }
}

/**
 * Throw [IllegalArgumentException] if key is not in bundle.
 *
 * @see Bundle.getString
 */
private fun Bundle.getStringOrThrow(key: String) =
    requireNotNull(getString(key)) { "Missing key '$key' in $this" }

/**
 * This is expected to be replaced by the navigation component, but for now handle navigation
 * manually.
 *
 * Instantiate this ViewModel at the scope that is fully-responsible for navigation, which in this
 * application is [MainActivity].
 *
 * This app has simplified navigation; the back stack is always [Home] or [Home, dest] and more
 * levels are not allowed. To use a similar pattern with a longer back stack, use a [ModelList] to
 * hold the back stack state.
 */
class NavigationViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    /**
     * Hold the current screen in an observable, restored from savedStateHandle after process
     * death.
     *
     * mutableStateOf is an observable similar to LiveData that's designed to be read by compose. It
     * supports observability via property delegate syntax as shown here.
     */
    var currentScreen: Screen by savedStateHandle.getMutableStateOf<Screen>(
        key = SIS_SCREEN,
        default = Screen.Home,
        save = { it.toBundle() },
        restore = { it.toScreen() }
    )
        private set // limit the writes to only inside this class.

    /**
     * Go back (always to [Home]).
     *
     * Returns true if this call caused user-visible navigation. Will always return false
     * when [currentScreen] is [Home].
     */
    @MainThread
    fun onBack(): Boolean {
        val wasHandled = currentScreen != Screen.Home
        currentScreen = Screen.Home
        return wasHandled
    }

    /**
     * Navigate to requested [Screen].
     *
     * If the requested screen is not [Home], it will always create a back stack with one element:
     * ([Home] -> [screen]). More back entries are not supported in this app.
     */
    @MainThread
    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }
}


/**
 * Return a [MutableState] that will automatically be saved in a [SavedStateHandle].
 *
 * This can be used from ViewModels to create a compose-observable value that survives rotation. It
 * supports arbitrary types with manual conversion to a [Bundle].
 *
 * @param save convert [T] to a [Bundle] for saving
 * @param restore restore a [T] from a [Bundle]
 */
fun <T> SavedStateHandle.getMutableStateOf(
    key: String,
    default: T,
    save: (T) -> Bundle,
    restore: (Bundle) -> T
): MutableState<T> {
    val bundle: Bundle? = get(key)
    val initial = if (bundle == null) {
        default
    } else {
        restore(bundle)
    }
    val state = mutableStateOf(initial)
    setSavedStateProvider(key) {
        save(state.value)
    }
    return state
}