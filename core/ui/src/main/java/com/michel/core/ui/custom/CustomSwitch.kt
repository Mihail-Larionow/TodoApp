package com.michel.core.ui.custom

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michel.core.ui.theme.TodoAppTheme

@Composable
fun CustomSwitch(
    modifier: Modifier = Modifier,
    hasDeadline: Boolean,
    enabled: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Switch(
        checked = hasDeadline,
        enabled = enabled,
        onCheckedChange = onCheckChange,
        colors = SwitchDefaults.colors(
            checkedIconColor = TodoAppTheme.color.blue,
            checkedThumbColor = TodoAppTheme.color.blue,
            checkedTrackColor = TodoAppTheme.color.blue.copy(
                alpha = 0.5f
            ),
            checkedBorderColor = TodoAppTheme.color.blue,
            uncheckedIconColor = TodoAppTheme.color.elevated,
            uncheckedThumbColor = TodoAppTheme.color.elevated,
            uncheckedTrackColor = TodoAppTheme.color.overlay,
            uncheckedBorderColor = TodoAppTheme.color.overlay
        ),
        modifier = modifier
    )
}