package io.github.kartoffelsup.nuntius.ui.user.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.api.user.result.FailedLogin
import io.github.kartoffelsup.nuntius.api.user.result.LoginResult
import io.github.kartoffelsup.nuntius.api.user.result.SuccessfulLogin
import io.github.kartoffelsup.nuntius.data.user.UserService
import io.github.kartoffelsup.nuntius.ui.AppState
import io.github.kartoffelsup.nuntius.ui.NavigationViewModel
import io.github.kartoffelsup.nuntius.ui.Screen
import io.github.kartoffelsup.nuntius.ui.common.FieldState
import io.github.kartoffelsup.nuntius.ui.common.ValidationResult
import io.github.kartoffelsup.nuntius.ui.components.CenteredRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ConnectException

data class LoginError(
    val text: String = ""
)

@Composable
fun UserLoginScreen(
    appState: AppState,
    innerPadding: PaddingValues,
    navigationViewModel: NavigationViewModel
) {
    appState.userData?.let {
        navigationViewModel.navigateTo(Screen.Home)
    }

    var loginError by remember { mutableStateOf(LoginError("")) }

    val coroutineScope = rememberCoroutineScope()

    val emailFieldState = FieldState(
        id = "emailField",
        value = TextFieldValue(""),
        validate = { value ->
            when {
                value.isEmpty() -> ValidationResult.Invalid("This field is required.")
                !value.matches("^.*@arml\\.com$".toRegex()) -> ValidationResult.Invalid("Must be a @arml.com Email")
                else -> ValidationResult.Valid
            }
        })

    val passwordFieldState = FieldState(
        id = "passwordField",
        value = TextFieldValue(""),
        validate = { value ->
            when {
                value.isEmpty() -> ValidationResult.Invalid("This field is required.")
                value.length < 3 -> ValidationResult.Invalid("Password must at least be 3 characters long.")
                else -> ValidationResult.Valid
            }
        })

    val serverConnectMessage = stringResource(R.string.server_connect_error)
    val internalError = stringResource(R.string.internal_error)

    val formState: LoginFormState = remember {
        LoginFormState(
            emailFieldState = emailFieldState,
            passwordFieldState = passwordFieldState
        ) {
            val mail = emailFieldState.value
            val pw = passwordFieldState.value
            // Reset errors on submit
            loginError = loginError.copy(text = "")

            coroutineScope.launch {
                val result: LoginResult = withContext(Dispatchers.IO) {
                    try {
                        UserService.login(mail.text, pw.text)
                    } catch (ioex: IOException) {
                        if (ioex is ConnectException) {
                            FailedLogin(serverConnectMessage)
                        } else {
                            println(ioex)
                            FailedLogin(internalError)
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    when (result) {
                        is SuccessfulLogin -> {
                            navigationViewModel.navigateTo(Screen.Home)
                        }
                        is FailedLogin -> {
                            loginError =
                                loginError.copy(text = "Login failed: ${result.message}")
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.padding(innerPadding)) {
        LoginForm(formState = formState)
        CenteredRow {
            if (loginError.text.isNotEmpty()) {
                Text(
                    text = loginError.text,
                    style = TextStyle.Default.copy(
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
    UserLoginScreen(AppState(), PaddingValues(), NavigationViewModel(SavedStateHandle()))
}
