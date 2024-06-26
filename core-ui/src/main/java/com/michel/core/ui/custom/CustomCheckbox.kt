package com.michel.core.ui.custom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.michel.core.ui.R

@Composable
fun ImageCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    checkedIcon: Painter,
    uncheckedIcon: Painter,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = modifier
            .clickable { onCheckedChange(!checked) }
    ) {
        Image(
            painter = uncheckedIcon,
            contentDescription = "unchecked",
            modifier = modifier
        )
        AnimatedVisibility(
            visible = checked,
            exit = shrinkOut(
                shrinkTowards = Alignment.TopStart
            ) + fadeOut(),
            modifier = modifier
        ) {
            Image(
                painter = checkedIcon,
                contentDescription = "checked",
                modifier = modifier
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun CustomCheckboxPreview() {
    val checked = true
    val checkedIcon = painterResource(R.drawable.ic_checked)
    val uncheckedIcon = painterResource(R.drawable.ic_unchecked)

    ImageCheckbox(
        checked = checked,
        checkedIcon = checkedIcon,
        uncheckedIcon = uncheckedIcon,
        onCheckedChange = { }
    )
}