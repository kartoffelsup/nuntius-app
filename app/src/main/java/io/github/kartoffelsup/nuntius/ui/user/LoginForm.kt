package io.github.kartoffelsup.nuntius.ui.user

import androidx.annotation.StringRes
import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.*
import androidx.ui.foundation.Border
import androidx.ui.foundation.DrawBorder
import androidx.ui.foundation.Icon
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.input.ImeAction
import androidx.ui.input.KeyboardType
import androidx.ui.input.PasswordVisualTransformation
import androidx.ui.input.VisualTransformation
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
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

@Model
class ValidationState(
    var valid: Boolean,
    var errorMessage: String?,
    val displayErrorPopup: Boolean = errorMessage != null
)

@Model
class FieldState constructor(
    val id: String,
    val validate: (String) -> ValidationResult,
    var value: String,
    val validationState: ValidationState = ValidationState(
        valid = false,
        errorMessage = null,
        displayErrorPopup = true
    ),
    var touched: Boolean = false,
    var focused: Boolean = false
)

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

        Row(LayoutWidth.Fill, arrangement = Arrangement.Center) {
            val formValid =         formState.fieldStates.all {
                it.validationState.valid
            }

            SubmitButton(
                state = SubmitButtonState(
                    enabled = formValid,
                    validate = {
                        formState.fieldStates.all {
                            it.validate(it.value)
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
    val focusManager = FocusManagerAmbient.current
    FormField(
        name = stringResource(R.string.prompt_email),
        fieldState = formState.emailFieldState,
        visualTransformation = null,
        vectorImage = vectorResource(id = R.drawable.ic_outline_mail_outline_24),
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next,
        onImeActionPerformed = { action ->
            if (action == ImeAction.Next) {
                focusManager.requestFocusById(formState.passwordFieldState.id)
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
        onImeActionPerformed = { action ->
            if (action == ImeAction.Done) {
                onSubmit()
            }
        },
        keyboardType = KeyboardType.Password
    )
}

@Model
class SubmitButtonState(
    var enabled: Boolean,
    val validate: () -> Boolean = {true}
)

@Composable
fun SubmitButton(
    state: SubmitButtonState,
    onSubmit: () -> Unit = {},
    modifier: Modifier = Modifier.None,
    children: @Composable() () -> Unit
) {
    val color = if (state.enabled) MaterialTheme.colors().primary else Color.LightGray
    Button(
        modifier =  modifier,
        backgroundColor = color,
        onClick = {
            state.validate()
            if (state.enabled) {
                onSubmit()
            }
        },
        enabled = state.enabled,
        border = Border(Dp.Hairline, Color.Black)
    ) {
        children()
    }
}

@Composable
private fun FormField(
    name: String,
    fieldState: FieldState,
    visualTransformation: VisualTransformation?,
    vectorImage: VectorAsset? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onImeActionPerformed: (ImeAction) -> Unit = {}
) {
    val colors = MaterialTheme.colors()
    val borderColor = when {
        fieldState.touched && fieldState.validationState.valid -> {
            colors.secondary
        }
        fieldState.focused -> colors.secondaryVariant
        else -> colors.error
    }

    Row(modifier = LayoutPadding(16.dp)) {
        Column {
            Row {
                vectorImage?.let {
                    Icon(icon = it)
                }
                Divider(LayoutWidth(3.dp))
                Text(name)
            }
            Row {
                TextField(
                    value = fieldState.value,
                    modifier = LayoutWeight(14f) + DrawBorder(
                        Border(2.dp, borderColor),
                        Underline
                    ),
                    focusIdentifier = fieldState.id,
                    keyboardType = keyboardType,
                    imeAction = imeAction,
                    onImeActionPerformed = onImeActionPerformed,
                    onFocus = {
                        fieldState.focused = true
                    },
                    onBlur = {
                        fieldState.focused = false
                    },
                    onValueChange = { newValue ->
                        fieldState.touched = true
                        val validationResult = fieldState.validate(newValue)
                        fieldState.validationState.valid =
                            validationResult == ValidationResult.Valid
                        fieldState.validationState.errorMessage =
                            validationResult.takeIf { it != ValidationResult.Valid }
                                ?.let { (it as ValidationResult.Invalid).reason }
                        fieldState.value = newValue
                    },
                    visualTransformation = visualTransformation
                )
                val iconColor =
                    if (fieldState.touched.not()) {
                        Color.LightGray
                    } else if (!fieldState.validationState.valid) {
                        MaterialTheme.colors().error
                    } else {
                        Color.Green
                    }

                val icon = @Composable {
                    Icon(
                        modifier = LayoutGravity.Center +
                                LayoutWeight(2f) +
                                LayoutPadding(2.dp) +
                                LayoutSize.Max(32.dp),
                        icon = vectorResource(id = R.drawable.ic_baseline_error_outline_24),
                        tint = iconColor
                    )
                }

                icon()
            }
            if (fieldState.touched && fieldState.validationState.displayErrorPopup) {
                Row {
                    NuntiusPopup(
                        fieldState.validationState.displayErrorPopup,
                        color = MaterialTheme.colors().background
                    ) {
                        Text(
                            text = fieldState.validationState.errorMessage ?: "",
                            style = currentTextStyle().copy(color = MaterialTheme.colors().error)
                        )
                    }
                }
            }
        }
    }
}
