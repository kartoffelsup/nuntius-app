package io.github.kartoffelsup.nuntius.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kartoffelsup.nuntius.R
import io.github.kartoffelsup.nuntius.ui.components.NuntiusPopup

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

class ValidationState(
    valid: Boolean = false,
    errorMessage: String? = null,
    val displayErrorPopup: Boolean = errorMessage != null
) {
    var valid by mutableStateOf(valid)
    var errorMessage by mutableStateOf(errorMessage)
}

data class SubmitButtonState(
    var enabled: Boolean,
    val validate: () -> Boolean = { true }
)

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
fun SubmitButton(
    state: SubmitButtonState,
    modifier: Modifier = Modifier,
    onSubmit: () -> Unit = {},
    children: @Composable() () -> Unit
) {
    val color = if (state.enabled) MaterialTheme.colors.primary else Color.LightGray
    Button(
        modifier = modifier.background(color),
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


@Composable
fun UsernameFormField(fieldState: FieldState) {
    FormField(
        name = stringResource(R.string.prompt_username),
        fieldState = fieldState,
        visualTransformation = null,
        vectorImage = painterResource(id = R.drawable.ic_outline_mail_outline_24),
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
        keyboardActions = KeyboardActions(onNext = {
            // TODO request focus for next field
        })
    )
}

@Composable
fun EmailFormField(fieldState: FieldState) {
    FormField(
        name = stringResource(R.string.prompt_email),
        fieldState = fieldState,
        visualTransformation = null,
        vectorImage = painterResource(id = R.drawable.ic_outline_mail_outline_24),
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next,
        keyboardActions = KeyboardActions(onNext = {
            // TODO request focus for next field
        })
    )
}

@Composable
fun PasswordFormField(fieldState: FieldState, onSubmit: () -> Unit) {
    FormField(
        name = stringResource(R.string.prompt_password),
        fieldState = fieldState,
        visualTransformation = PasswordVisualTransformation(),
        vectorImage = painterResource(id = R.drawable.ic_outline_security_24),
        imeAction = ImeAction.Done,
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        keyboardType = KeyboardType.Password
    )
}

@Composable
private fun FormField(
    name: String,
    fieldState: FieldState,
    visualTransformation: VisualTransformation?,
    vectorImage: Painter? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val colors = MaterialTheme.colors
    val borderColor = when {
        fieldState.touched && !fieldState.validationState.valid -> colors.error
        fieldState.focused -> colors.secondaryVariant
        else -> colors.secondary
    }

    Row(modifier = Modifier.padding(16.dp)) {
        Column {
            Row {
                vectorImage?.let {
                    Icon(painter = it, contentDescription = null)
                }
                Divider(Modifier.width(3.dp))
                Text(name)
            }
            Row {
                TextField(
                    value = fieldState.value,
                    modifier = Modifier
                        .weight(14f)
                        .border(BorderStroke(2.dp, borderColor))
                        .onFocusChanged {
                            if (fieldState.focused && !it.isFocused) {
                                fieldState.touched = true
                            }

                            fieldState.focused = it.isFocused
                        },
                    keyboardOptions = KeyboardOptions(
                        imeAction = imeAction,
                        keyboardType = keyboardType
                    ),
                    keyboardActions = keyboardActions,
                    onValueChange = { newValue: TextFieldValue ->
                        fieldState.touched = true
                        when (val validationResult = fieldState.validate(newValue.text)) {
                            is ValidationResult.Valid -> {
                                fieldState.validationState.valid = true
                                fieldState.validationState.errorMessage = null
                            }
                            is ValidationResult.Invalid -> {
                                fieldState.validationState.valid = false
                                fieldState.validationState.errorMessage = validationResult.reason
                            }
                        }
                        fieldState.value = newValue
                    },
                    visualTransformation = visualTransformation ?: VisualTransformation.None
                )

                val iconColor =
                    if (!fieldState.touched) {
                        Color.LightGray
                    } else if (!fieldState.validationState.valid) {
                        MaterialTheme.colors.error
                    } else {
                        Color.Green
                    }

                val icon = @Composable {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .then(Modifier.weight(2f))
                            .then(Modifier.padding(2.dp))
                            .then(Modifier.sizeIn(maxWidth = 32.dp, maxHeight = 32.dp)),
                        painter = painterResource(id = R.drawable.ic_baseline_error_outline_24),
                        contentDescription = null,
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
                            style = TextStyle.Default.copy(color = MaterialTheme.colors.error)
                        )
                    }
                }
            }
        }
    }
}
