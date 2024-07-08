package com.michel.core.ui.custom

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.michel.core.ui.R
import com.michel.core.ui.theme.TodoAppTheme

/**
 * Custom text field function with lovely colors
 */
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String,
    shape: Shape,
    enabled: Boolean = true,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        enabled = enabled,
        shape = shape,
        minLines = 5,
        textStyle = TodoAppTheme.typography.body,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = TodoAppTheme.color.backSecondary,
            focusedContainerColor = TodoAppTheme.color.backSecondary,
            disabledContainerColor = TodoAppTheme.color.disable,
            errorContainerColor = TodoAppTheme.color.backSecondary,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = TodoAppTheme.color.red,
            focusedTextColor = TodoAppTheme.color.primary,
            unfocusedTextColor = TodoAppTheme.color.primary,
            disabledTextColor = TodoAppTheme.color.tertiary,
            errorTextColor = TodoAppTheme.color.red
        ),
        onValueChange = { onValueChanged(it) },
        placeholder = {
            Text(
                text = stringResource(com.michel.core.ui.R.string.placeholder),
                style = TodoAppTheme.typography.body
            )
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun CustomTextFieldPreview() {
    val text = stringResource(R.string.longText)
    val shape = RectangleShape

    TodoAppTheme {
        CustomTextField(
            text = text,
            shape = shape,
            enabled = false,
            onValueChanged = { }
        )
    }
}