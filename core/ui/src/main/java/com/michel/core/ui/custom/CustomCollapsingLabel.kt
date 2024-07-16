package com.michel.core.ui.custom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michel.core.ui.theme.TodoAppTheme
import kotlinx.coroutines.delay

@Composable
fun CustomCollapsingLabel(
    modifier: Modifier = Modifier,
    containerColor: Color,
    containerShape: Shape,
    textStyle: TextStyle,
    textColor: Color,
    state: Boolean,
    text: String,
    size: Dp,
) {

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        expanded = true
        delay(3000)
        expanded = false
    }

    Box(
        modifier = Modifier
            .heightIn(min = size)
            .widthIn(min = size)
            .background(
                shape = containerShape,
                color = containerColor
            )
    ) {
        AnimatedVisibility(visible = expanded) {
            Text(
                text = text,
                color = textColor,
                style = textStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(all = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollapsingLabelPreview() {
    TodoAppTheme {
        CustomCollapsingLabel(
            containerColor = TodoAppTheme.color.green,
            containerShape = TodoAppTheme.shape.container,
            textStyle = TodoAppTheme.typography.body,
            textColor = TodoAppTheme.color.primary,
            text = "some text",
            state = true,
            size = 16.dp
        )
    }
}