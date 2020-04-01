
package io.github.kartoffelsup.nuntius.ui.user

import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.currentTextStyle
import androidx.ui.geometry.Rect
import androidx.ui.graphics.Outline
import androidx.ui.graphics.Shape
import androidx.ui.layout.Column
import androidx.ui.material.MaterialTheme
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Density
import androidx.ui.unit.PxSize
import io.github.kartoffelsup.nuntius.api.user.result.FailedLogin
import io.github.kartoffelsup.nuntius.api.user.result.LoginResult
import io.github.kartoffelsup.nuntius.api.user.result.SuccessfulLogin
import io.github.kartoffelsup.nuntius.data.user.UserService
import io.github.kartoffelsup.nuntius.ui.AppState
import io.github.kartoffelsup.nuntius.ui.components.CenteredRow
import io.github.kartoffelsup.nuntius.ui.Screen
import io.github.kartoffelsup.nuntius.ui.navigateTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Model
class LoginError(
    var text: String = ""
)

object Underline : Shape {
    override fun createOutline(
        size: PxSize,
        density: Density
    ): Outline {
        return Outline.Rectangle(
            Rect(
                0f,
                size.height.value - density.density,
                size.width.value,
                size.height.value
            )
        )
    }
}

@Composable
fun UserLoginScreen(appState: AppState, modifier: Modifier) {
    appState.userData?.let {
        navigateTo(Screen.Home)
    }

    val loginError = LoginError("")

    val emailFieldState =
        FieldState(
            id = "emailField",
            value = "",
            validate = { value ->
                when {
                    value.isEmpty() -> ValidationResult.Invalid("This field is required.")
                    !value.matches("^.*@arml\\.com$".toRegex()) -> ValidationResult.Invalid("Must be a @arml.com Email")
                    else -> ValidationResult.Valid
                }
            })

    val passwordFieldState =
        FieldState(
            id = "passwordField",
            value = "",
            validate = { value ->
                when {
                    value.isEmpty() -> ValidationResult.Invalid("This field is required.")
                    value.length < 3 -> ValidationResult.Invalid("Password must at least be 3 characters long.")
                    else -> ValidationResult.Valid
                }
            })

    val formState =
        LoginFormState(
            emailFieldState = emailFieldState,
            passwordFieldState = passwordFieldState
        ) {
            val mail = emailFieldState.value
            val pw = passwordFieldState.value

            GlobalScope.launch {
                val result: LoginResult = UserService.login(mail, pw)
                withContext(Dispatchers.Main) {
                    when (result) {
                        is SuccessfulLogin -> {
                            navigateTo(Screen.Home)
                        }
                        is FailedLogin -> loginError.text =
                            "Login failed: ${result.message}"
                    }
                }
            }
        }

    Column(modifier = modifier) {
        LoginForm(formState = formState)
        CenteredRow {
            if (loginError.text.isNotEmpty()) {
                Text(
                    text = loginError.text,
                    style = currentTextStyle().copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.error
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    UserLoginScreen(AppState(), Modifier.None)
}
