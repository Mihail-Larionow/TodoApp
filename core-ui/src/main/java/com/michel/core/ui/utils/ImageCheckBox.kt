package com.michel.core.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun ImageCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    iconChecked: Int,
    iconUnchecked: Int,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = modifier
            .clickable { onCheckedChange(!checked) }
    ) {
        Image(
            modifier = modifier,
            painter = painterResource(id = iconUnchecked),
            contentDescription = "unchecked"
        )
        AnimatedVisibility(
            modifier = modifier,
            visible = checked,
            exit = shrinkOut(
                shrinkTowards = Alignment.TopStart
            ) + fadeOut()
        ) {
            Image(
                modifier = modifier,
                painter = painterResource(id = iconChecked),
                contentDescription = "checked"
            )
        }
    }
}