package com.michel.core.ui.ripple

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class ColoredRippleTheme(private val color: Color): RippleTheme {

    @Composable
    override fun defaultColor(): Color = color

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        color,
        lightTheme = isSystemInDarkTheme()
    )

}