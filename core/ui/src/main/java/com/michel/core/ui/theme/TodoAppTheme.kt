package com.michel.core.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    title = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    button = TextStyle(
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
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
    container = RoundedCornerShape(8.dp)
)

private val size = TodoAppSize(
    toolBar = 56.dp,
    textField = 128.dp,
    standardIcon = 28.dp,
    smallIcon = 20.dp
)

/**
 * Provides application theme (colorScheme, shapes, textStyles, etc)
 */
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

@Preview
@Composable
private fun LightThemePreview() {
    Column {
        Text(
            text = "Палитра - светлая тема",
            fontSize = 10.sp,
            color = lightColorScheme.white,
            modifier = Modifier.padding(4.dp)
        )
        Row(Modifier.padding(4.dp)) {
            ColorBox(
                color = lightColorScheme.separator,
                text = "Support [Light] / Separator\n#33000000",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = lightColorScheme.overlay,
                text = "Support [Light] / Overlay\n#0F000000",
                textColor = lightColorScheme.white
            )
        }
        Row(Modifier.padding(4.dp)) {
            ColorBox(
                color = lightColorScheme.primary,
                text = "Label [Light] / Primary\n#000000",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = lightColorScheme.secondary,
                text = "Label [Light] / Secondary\n#99000000",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = lightColorScheme.tertiary,
                text = "Label [Light] / Tertiary\n#4D000000",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = lightColorScheme.disable,
                text = "Label [Light] / Disable\n#26000000",
                textColor = lightColorScheme.white
            )
        }
        Row(Modifier.padding(4.dp)) {
            ColorBox(
                color = lightColorScheme.red,
                text = "Color [Light] / Red\n#FF3B30",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = lightColorScheme.green,
                text = "Color [Light] / Green\n#32C759",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = lightColorScheme.blue,
                text = "Color [Light] / Blue\n#007AFF",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = lightColorScheme.gray,
                text = "Color [Light] / Gray\n#8E8E93",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = lightColorScheme.lightGray,
                text = "Color [Light] / Gray Light\n#D1D1D6",
                textColor = lightColorScheme.primary
            )
            ColorBox(
                color = lightColorScheme.white,
                text = "Color [Light] / White\n#FFFFFF",
                textColor = lightColorScheme.primary
            )
        }
        Row(Modifier.padding(4.dp)) {
            ColorBox(
                color = lightColorScheme.backPrimary,
                text = "Back [Light] / Primary\n#F7F6F2",
                textColor = lightColorScheme.primary
            )
            ColorBox(
                color = lightColorScheme.backSecondary,
                text = "Back [Light] / Secondary\n#FFFFFF",
                textColor = lightColorScheme.primary
            )
            ColorBox(
                color = lightColorScheme.elevated,
                text = "Back [Light] / Elevated\n#FFFFFF",
                textColor = lightColorScheme.primary
            )
        }
    }
}

@Preview
@Composable
private fun DarkThemePreview() {
    Column {
        Text(
            text = "Палитра - темная тема",
            fontSize = 10.sp,
            color = lightColorScheme.white,
            modifier = Modifier.padding(4.dp)
        )
        Row(Modifier.padding(4.dp)) {
            ColorBox(
                color = darkColorScheme.separator,
                text = "Support [Dark] / Separator\n#33FFFFFF",
                textColor = lightColorScheme.primary
            )
            ColorBox(
                color = darkColorScheme.overlay,
                text = "Support [Dark] / Overlay\n#52000000",
                textColor = lightColorScheme.white
            )
        }
        Row(Modifier.padding(4.dp)) {
            ColorBox(
                color = darkColorScheme.primary,
                text = "Label [Dark] / Primary\n#FFFFFF",
                textColor = lightColorScheme.primary
            )
            ColorBox(
                color = darkColorScheme.secondary,
                text = "Label [Dark] / Secondary\n#99FFFFFF",
                textColor = lightColorScheme.primary
            )
            ColorBox(
                color = darkColorScheme.tertiary,
                text = "Label [Dark] / Tertiary\n#66FFFFFF",
                textColor = lightColorScheme.primary
            )
            ColorBox(
                color = darkColorScheme.disable,
                text = "Label [Dark] / Disable\n#26FFFFFF",
                textColor = lightColorScheme.primary
            )
        }
        Row(Modifier.padding(4.dp)) {
            ColorBox(
                color = darkColorScheme.red,
                text = "Color [Dark] / Red\n#FF453A",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = darkColorScheme.green,
                text = "Color [Dark] / Green\n#32D74B",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = darkColorScheme.blue,
                text = "Color [Dark] / Blue\n#0A84FF",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = darkColorScheme.gray,
                text = "Color [Dark] / Gray\n#8E8E93",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = darkColorScheme.lightGray,
                text = "Color [Dark] / Gray Light\n#48484A",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = darkColorScheme.white,
                text = "Color [Dark] / White\n#FFFFFF",
                textColor = lightColorScheme.primary
            )
        }
        Row(Modifier.padding(4.dp)) {
            ColorBox(
                color = darkColorScheme.backPrimary,
                text = "Back [Dark] / Primary\n#161618",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = darkColorScheme.backSecondary,
                text = "Back [Dark] / Secondary\n#252528",
                textColor = lightColorScheme.white
            )
            ColorBox(
                color = darkColorScheme.elevated,
                text = "Back [Dark] / Elevated\n#3C3C3F",
                textColor = lightColorScheme.white
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TextStylesPreview() {
    Column {
        Text(
            text = "Large title - 32sp",
            style = typography.largeTitle,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Title - 20sp",
            style = typography.title,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "BUTTON - 14sp",
            style = typography.button,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Body - 16sp",
            style = typography.body,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Subhead - 14sp",
            style = typography.subhead,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun ColorBox(
    color: Color,
    text: String,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .size(
                width = 64.dp,
                height = 30.dp
            )
            .background(color = color)
    ) {
        Text(
            text = text,
            fontSize = 4.sp,
            color = textColor,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(2.dp)
        )
    }
}