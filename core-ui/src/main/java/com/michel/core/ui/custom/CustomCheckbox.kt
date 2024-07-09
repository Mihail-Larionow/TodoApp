package com.michel.core.ui.custom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.michel.core.ui.R
import com.michel.core.ui.theme.TodoAppTheme

/**
 * Custom checkbox where you can put your images
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    checkedIcon: Painter,
    checkedTint: Color,
    uncheckedIcon: Painter,
    uncheckedTint: Color,
    disabledTint: Color,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        IconButton(
            enabled = enabled,
            onClick = { onCheckedChange(!checked) },
            modifier = modifier
        ) {
            Content(
                checkedIcon = checkedIcon,
                checkedTint = checkedTint,
                uncheckedIcon = uncheckedIcon,
                uncheckedTint = uncheckedTint,
                disabledTint = disabledTint,
                checked = checked,
                enabled = enabled
            )
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    checkedIcon: Painter,
    checkedTint: Color,
    uncheckedIcon: Painter,
    uncheckedTint: Color,
    disabledTint: Color,
    checked: Boolean,
    enabled: Boolean,
) {
    val uncheckedTint = animateColorAsState(
        targetValue = if(enabled) uncheckedTint else disabledTint
    )

    val checkedTint = animateColorAsState(
        targetValue = if(enabled) checkedTint else disabledTint
    )

    AnimatedVisibility(
        visible = !checked,
        exit = scaleOut(),
        enter = scaleIn(),
        modifier = modifier
    ) {
        Icon(
            painter = uncheckedIcon,
            contentDescription = stringResource(R.string.unchecked),
            tint = uncheckedTint.value,
            modifier = modifier
        )
    }
    AnimatedVisibility(
        visible = checked,
        exit = scaleOut(),
        enter = scaleIn(),
        modifier = modifier
    ) {
        Icon(
            painter = checkedIcon,
            contentDescription = stringResource(R.string.checked),
            tint = checkedTint.value,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomCheckboxPreview() {
    val checked = true
    val enabled = false
    val checkedIcon = painterResource(R.drawable.ic_checked)
    val uncheckedIcon = painterResource(R.drawable.ic_unchecked)

    TodoAppTheme {
        ImageCheckbox(
            checked = checked,
            checkedIcon = checkedIcon,
            checkedTint = TodoAppTheme.color.green,
            uncheckedIcon = uncheckedIcon,
            uncheckedTint = TodoAppTheme.color.tertiary,
            disabledTint = TodoAppTheme.color.disable,
            enabled = enabled,
            onCheckedChange = { }
        )
    }
}