package com.michel.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

/**
 * Data class of application color scheme
 */
data class TodoAppColorScheme(
    val separator: Color,
    val overlay: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val disable: Color,
    val red: Color,
    val green: Color,
    val blue: Color,
    val gray: Color,
    val lightGray: Color,
    val white: Color,
    val backPrimary: Color,
    val backSecondary: Color,
    val elevated: Color
)

/**
 * Data class of application text styles
 */
data class TodoAppTypography(
    val largeTitle: TextStyle,
    val title: TextStyle,
    val button: TextStyle,
    val body: TextStyle,
    val subhead: TextStyle
)

/**
 * Data class of application shapes
 */
data class TodoAppShape(
    val container: Shape
)

/**
 * Data class of application color scheme
 */
data class TodoAppSize(
    val toolBar: Dp,
    val textField: Dp,
    val standardIcon: Dp,
    val smallIcon: Dp
)

val LocalTodoAppColorScheme = staticCompositionLocalOf {
    TodoAppColorScheme(
        separator = Color.Unspecified,
        overlay = Color.Unspecified,
        primary = Color.Unspecified,
        secondary = Color.Unspecified,
        tertiary = Color.Unspecified,
        disable = Color.Unspecified,
        red = Color.Unspecified,
        green = Color.Unspecified,
        blue = Color.Unspecified,
        gray = Color.Unspecified,
        lightGray = Color.Unspecified,
        white = Color.Unspecified,
        backPrimary = Color.Unspecified,
        backSecondary = Color.Unspecified,
        elevated = Color.Unspecified
    )
}

val LocalTodoAppTypography = staticCompositionLocalOf {
    TodoAppTypography(
        largeTitle = TextStyle.Default,
        title = TextStyle.Default,
        button = TextStyle.Default,
        body = TextStyle.Default,
        subhead = TextStyle.Default
    )
}

val LocalTodoAppShape = staticCompositionLocalOf {
    TodoAppShape(
        container = RectangleShape
    )
}

val LocalTodoAppSize = staticCompositionLocalOf {
    TodoAppSize(
        toolBar = Dp.Unspecified,
        textField = Dp.Unspecified,
        standardIcon = Dp.Unspecified,
        smallIcon = Dp.Unspecified
    )
}