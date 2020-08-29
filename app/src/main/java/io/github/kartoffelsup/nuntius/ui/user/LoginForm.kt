package io.github.kartoffelsup.nuntius.ui.user

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focusObserver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.ui.components.NuntiusPopup

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

class LoginFormState(
    val emailFieldState: FieldState,
    val passwordFieldState: FieldState,
    val fieldStates: List<FieldState> = listOf(
        emailFieldState,
        passwordFieldState
    ),
    val onSubmit: () -> Unit
)

class ValidationState(
    valid: Boolean,
    errorMessage: String?,
    val displayErrorPopup: Boolean = errorMessage != null
) {
    var valid by mutableStateOf(valid)
    var errorMessage by mutableStateOf(errorMessage)
}

class FieldState(
    val id: String,
    val validate: (String) -> ValidationResult,
    value: TextFieldValue,
    val validationState: ValidationState = ValidationState(
        valid = false,
        errorMessage = null,
        displayErrorPopup = true
    ),
    touched: Boolean = false,
    focused: Boolean = false
) {
    var value by mutableStateOf(value)
    var touched by mutableStateOf(touched)
    var focused by mutableStateOf(focused)
}

@Composable
fun LoginForm(formState: LoginFormState) {
    Column {
        EmailFormField(
            formState
        )
        PasswordFormField(
            formState,
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
                Text(stringResource(R.string.common_signin_button_text))
            }
        }
    }
}

@Composable
fun EmailFormField(formState: LoginFormState) {
    FormField(
        name = stringResource(R.string.prompt_email),
        fieldState = formState.emailFieldState,
        visualTransformation = null,
        vectorImage = vectorResource(id = R.drawable.ic_outline_mail_outline_24),
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next,
        onImeActionPerformed = { action, keyboard ->
            if (action == ImeAction.Next) {
                // TODO request focus for password field via modifier?!
            }
        }
    )
}

@Composable
fun PasswordFormField(formState: LoginFormState, onSubmit: () -> Unit) {
    FormField(
        name = stringResource(R.string.prompt_password),
        fieldState = formState.passwordFieldState,
        visualTransformation = PasswordVisualTransformation(),
        vectorImage = vectorResource(id = R.drawable.ic_outline_security_24),
        imeAction = ImeAction.Done,
        onImeActionPerformed = { action, keyboard ->
            if (action == ImeAction.Done) {
                onSubmit()
            }
        },
        keyboardType = KeyboardType.Password
    )
}

data class SubmitButtonState(
    var enabled: Boolean,
    val validate: () -> Boolean = { true }
)

@Composable
fun SubmitButton(
    state: SubmitButtonState,
    onSubmit: () -> Unit = {},
    modifier: Modifier = Modifier,
    children: @Composable() () -> Unit
) {
    val color = if (state.enabled) MaterialTheme.colors.primary else Color.LightGray
    Button(
        modifier = modifier,
        backgroundColor = color,
        onClick = {
            state.validate()
            if (state.enabled) {
                onSubmit()
            }
        },
        enabled = state.enabled,
        border = BorderStroke(Dp.Hairline, Color.Black)
    ) {
        children()
    }
}

@OptIn(ExperimentalFocus::class)
@Composable
private fun FormField(
    name: String,
    fieldState: FieldState,
    visualTransformation: VisualTransformation?,
    vectorImage: VectorAsset? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onImeActionPerformed: (ImeAction, SoftwareKeyboardController?) -> Unit = { _, _ -> }
) {
    val colors = MaterialTheme.colors
    val borderColor = when {
        fieldState.touched && fieldState.validationState.valid -> {
            colors.secondary
        }
        fieldState.focused -> colors.secondaryVariant
        else -> colors.error
    }

    Row(modifier = Modifier.padding(16.dp)) {
        Column {
            Row {
                vectorImage?.let {
                    Icon(asset = it)
                }
                Divider(Modifier.preferredWidth(3.dp))
                Text(name)
            }
            Row {
                TextField(
                    value = fieldState.value,
                    label = {},
                    modifier = Modifier.weight(14f)
                        .border(BorderStroke(2.dp, borderColor))
                        .focusObserver { fieldState.focused = it == FocusState.Active },
                    keyboardType = keyboardType,
                    imeAction = imeAction,
                    onImeActionPerformed = onImeActionPerformed,
                    onValueChange = { newValue ->
                        fieldState.touched = true
                        val validationResult = fieldState.validate(newValue.text)
                        fieldState.validationState.valid =
                            validationResult == ValidationResult.Valid
                        fieldState.validationState.errorMessage =
                            validationResult.let { (it as? ValidationResult.Invalid)?.reason }
                        fieldState.value = newValue
                    },
                    visualTransformation = visualTransformation ?: VisualTransformation.None
                )
                val iconColor =
                    if (fieldState.touched.not()) {
                        Color.LightGray
                    } else if (!fieldState.validationState.valid) {
                        MaterialTheme.colors.error
                    } else {
                        Color.Green
                    }

                val icon = @Composable {
                    Icon(
                        modifier = Modifier.gravity(Alignment.CenterVertically)
                            .then(Modifier.weight(2f))
                            .then(Modifier.padding(2.dp))
                            .then(Modifier.preferredSizeIn(maxWidth = 32.dp, maxHeight = 32.dp)),
                        asset = vectorResource(id = R.drawable.ic_baseline_error_outline_24),
                        tint = iconColor
                    )
                }

                icon()
            }
            if (fieldState.touched && fieldState.validationState.displayErrorPopup) {
                Row {
                    NuntiusPopup(
                        fieldState.validationState.displayErrorPopup,
                        color = MaterialTheme.colors.background
                    ) {
                        Text(
                            text = fieldState.validationState.errorMessage ?: "",
                            style = currentTextStyle().copy(color = MaterialTheme.colors.error)
                        )
                    }
                }
            }
        }
    }
}
