package com.michel.core.ui.utils

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michel.core.ui.theme.TodoAppTheme

@Composable
fun SpecialDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp,
    color: Color = TodoAppTheme.color.separator
) {
    Spacer(modifier = Modifier.width(startIndent))
    Divider(
        thickness = thickness,
        color = color,
        modifier = modifier
    )
    Spacer(modifier = Modifier.width(startIndent))
}