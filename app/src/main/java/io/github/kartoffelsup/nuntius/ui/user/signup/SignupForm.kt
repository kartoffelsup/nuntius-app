package io.github.kartoffelsup.nuntius.ui.user.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.ui.common.*

class SignupFormState(
    val usernameFieldState: FieldState,
    val emailFieldState: FieldState,
    val passwordFieldState: FieldState,
    val fieldStates: List<FieldState> = listOf(
        usernameFieldState,
        emailFieldState,
        passwordFieldState
    ),
    val onSubmit: () -> Unit
)

@Composable
fun SignupForm(formState: SignupFormState) {
    Column {
        UsernameFormField(
            formState.usernameFieldState
        )

        EmailFormField(
            formState.emailFieldState
        )

        PasswordFormField(
            formState.passwordFieldState,
            formState.onSubmit
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            val formValid = formState.fieldStates.all {
                it.validationState.valid
            }

            SubmitButton(
                state = SubmitButtonState(
                    enabled = formValid,
                    validate = {
                        formState.fieldStates.all {
                            it.validate(it.value.text)
                            it.touched = true
                            it.validationState.valid
                        }
                    }
                ),
                onSubmit = formState.onSubmit
            ) {
                Text(stringResource(R.string.prompt_signup))
            }
        }
    }
}
