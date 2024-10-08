package com.michel.core.ui.custom

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michel.core.ui.theme.TodoAppTheme

/**
 * Custom divider function
 */
@Preview(showBackground = true)
@Composable
fun TodoDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp,
    color: Color = TodoAppTheme.color.separator
) {
    Spacer(modifier = Modifier.width(startIndent))
    HorizontalDivider(
        thickness = thickness,
        color = color,
        modifier = modifier
    )
    Spacer(modifier = Modifier.width(startIndent))
}

