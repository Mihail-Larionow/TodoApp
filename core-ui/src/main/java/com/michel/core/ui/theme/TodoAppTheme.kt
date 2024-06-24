package com.michel.core.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val darkColorScheme = TodoAppColorScheme(
    separator = SeparatorDark,
    overlay = OverlayDark,
    primary = White,
    secondary = SecondaryDark,
    tertiary = TertiaryDark,
    disable = DisableDark,
    red = RedDark,
    green = GreenDark,
    blue = BlueDark,
    gray = Gray,
    lightGray = DarkGray,
    white = White,
    backPrimary = BackPrimaryDark,
    backSecondary = BackSecondaryDark,
    elevated = ElevatedDark
)

private val lightColorScheme = TodoAppColorScheme(
    separator = SeparatorLight,
    overlay = OverlayLight,
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight,
    disable = DisableLight,
    red = RedLight,
    green = GreenLight,
    blue = BlueLight,
    gray = Gray,
    lightGray = LightGray,
    white = White,
    backPrimary = BackPrimaryLight,
    backSecondary = White,
    elevated = White
)

private val typography = TodoAppTypography(
    largeTitle = TextStyle(
        fontSize = 32.sp
    ),
    title = TextStyle(
        fontSize = 20.sp
    ),
    button = TextStyle(
        fontSize = 14.sp
    ),
    body = TextStyle(
        fontSize = 16.sp
    ),
    subhead = TextStyle(
        fontSize = 14.sp
    )
)

private val shape = TodoAppShape(
    container = RoundedCornerShape(16.dp)
)

private val size = TodoAppSize(
    toolBar = 56.dp,
    textField = 128.dp,
    standardIcon = 28.dp,
    smallIcon = 20.dp
)

@Composable
fun TodoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val color = when {
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    val rippleIndication = rememberRipple()
    CompositionLocalProvider(
        LocalTodoAppColorScheme provides color,
        LocalTodoAppTypography provides typography,
        LocalTodoAppShape provides shape,
        LocalTodoAppSize provides size,
        LocalIndication provides rippleIndication,
        content = content
    )

}

object TodoAppTheme {
    val color: TodoAppColorScheme
        @Composable get() = LocalTodoAppColorScheme.current

    val typography: TodoAppTypography
        @Composable get() = LocalTodoAppTypography.current

    val shape: TodoAppShape
        @Composable get() = LocalTodoAppShape.current

    val size: TodoAppSize
        @Composable get() = LocalTodoAppSize.current

}