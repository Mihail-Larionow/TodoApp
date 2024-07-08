package com.michel.core.ui.extensions

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

// Добавляет тень снизу под элементом
fun Modifier.bottomShadow(
    shadow: Dp,
    shape: Shape = RectangleShape
) = this
    .clip(
        shape = GenericShape { size, _ ->
            lineTo(size.width, 0f)
            lineTo(size.width, Float.MAX_VALUE)
            lineTo(0f, Float.MAX_VALUE)
        }
    )
    .shadow(
        elevation = shadow,
        shape = shape
    )