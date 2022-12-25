package io.github.kartoffelsup.nuntius.ui.user.signup

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
import arrow.core.Either
import arrow.core.left
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.api.user.result.CreateUserResult
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

data class SignupError(
    val text: String = ""
)

@Composable
fun SignupScreen(
    appState: AppState,
    innerPadding: PaddingValues,
    navigationViewModel: NavigationViewModel
) {
    appState.userData?.let {
        navigationViewModel.navigateTo(Screen.Home)
    }

    var signupError by remember { mutableStateOf(SignupError("")) }

    val coroutineScope = rememberCoroutineScope()

    val usernameFieldState = FieldState(
        id = "usernameField",
        value = TextFieldValue(""),
        validate = { value ->
            when {
                value.isEmpty() -> ValidationResult.Invalid("This field is required.")
                value.length < 3 -> ValidationResult.Invalid("Username must at least be 3 characters long.")
                else -> ValidationResult.Valid
            }
        })

    val emailFieldState =
        FieldState(
            id = "emailField",
            value = TextFieldValue(""),
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
    val formState: SignupFormState = remember {
        SignupFormState(
            usernameFieldState = usernameFieldState,
            emailFieldState = emailFieldState,
            passwordFieldState = passwordFieldState
        ) {
            val username = usernameFieldState.value
            val mail = emailFieldState.value
            val pw = passwordFieldState.value
            // Reset errors on submit
            signupError = signupError.copy(text = "")

            coroutineScope.launch {
                val result: Either<String, CreateUserResult> = withContext(Dispatchers.IO) {
                    try {
                        UserService.signup(username.text, mail.text, pw.text)
                    } catch (ioex: IOException) {
                        if (ioex is ConnectException) {
                            serverConnectMessage.left()
                        } else {
                            println(ioex)
                            internalError.left()
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    result.fold(
                        ifLeft = { message ->
                            signupError = signupError.copy(text = "Sign up failed: $message")
                        },
                        ifRight = { println(it) }
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(innerPadding)) {
        SignupForm(formState = formState)
        CenteredRow {
            if (signupError.text.isNotEmpty()) {
                Text(
                    text = signupError.text,
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
fun SignupPreview() {
    SignupScreen(AppState(), PaddingValues(), NavigationViewModel(SavedStateHandle()))
}
