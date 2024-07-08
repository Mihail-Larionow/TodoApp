package com.michel.core.ui.custom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.michel.core.ui.R
import com.michel.core.ui.theme.TodoAppTheme

/**
 * Custom checkbox where you can put your images
 */
@Composable
fun ImageCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    checkedIcon: Painter,
    uncheckedIcon: Painter,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = { onCheckedChange(!checked) }
            )
    ) {
        Content(
            checkedIcon = checkedIcon,
            uncheckedIcon = uncheckedIcon,
            checked = checked
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    checkedIcon: Painter,
    uncheckedIcon: Painter,
    checked: Boolean
) {
    Image(
        painter = uncheckedIcon,
        contentDescription = stringResource(R.string.unchecked),
        modifier = modifier
    )
    AnimatedVisibility(
        visible = checked,
        exit = shrinkOut(
            shrinkTowards = Alignment.TopStart
        ) + fadeOut(),
        modifier = modifier
    ) {
        Image(
            painter = checkedIcon,
            contentDescription = stringResource(R.string.checked),
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomCheckboxPreview() {
    val checked = true
    val enabled = true
    val checkedIcon = painterResource(R.drawable.ic_checked)
    val uncheckedIcon = painterResource(R.drawable.ic_unchecked)

    TodoAppTheme {
        ImageCheckbox(
            checked = checked,
            checkedIcon = checkedIcon,
            uncheckedIcon = uncheckedIcon,
            enabled = enabled,
            onCheckedChange = { }
        )
    }
}