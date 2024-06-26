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

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String,
    shape: Shape,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = {
            onValueChanged(it)
        },
        shape = shape,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = TodoAppTheme.color.backSecondary,
            focusedContainerColor = TodoAppTheme.color.backSecondary,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            errorContainerColor = TodoAppTheme.color.backSecondary,
            errorIndicatorColor = TodoAppTheme.color.red,
            focusedTextColor = TodoAppTheme.color.primary,
            unfocusedTextColor = TodoAppTheme.color.primary
        ),
        placeholder = {
            Text(
                text = stringResource(com.michel.core.ui.R.string.placeholder),
                style = TodoAppTheme.typography.body
            )
        },
        minLines = 5,
        textStyle = TodoAppTheme.typography.body,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun CustomTextFieldPreview() {
    val text = stringResource(R.string.longText)
    val shape = RectangleShape
    CustomTextField(
        text = text,
        shape = shape,
        onValueChanged = { }
    )
}