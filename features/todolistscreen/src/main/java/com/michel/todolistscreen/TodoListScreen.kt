package com.michel.todolistscreen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.michel.todolistscreen.ui.theme.TodoAppTheme

private val titleHeight = 56.dp
private val scrollSize = 96.dp

private val minTitlePadding = 16.dp
private val maxTitlePadding = 64.dp

private val minFontSize = 20.sp
private val maxFontSize = 32.sp

private val maxTitleOffset = scrollSize

@Preview(showBackground = true)
@Composable
fun TodoList(modifier: Modifier = Modifier) {
    TodoAppTheme{
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = TodoAppTheme.colorScheme.backPrimary
                )
        ) {
            val scroll = rememberScrollState(0)
            Body(scroll = scroll)
            Title{ scroll.value }
        }
    }
}

@Composable
private fun Body(
    scroll: ScrollState
) {
    Column {
        Column(
            modifier = Modifier.verticalScroll(scroll)
        ) {
            Spacer(modifier = Modifier.height(scrollSize + titleHeight + 16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        color = TodoAppTheme.colorScheme.backSecondary,
                        shape = TodoAppTheme.shape.container
                    )
            ) {
                repeat(20) {
                    PreviewTodoItemModel()
                }
            }
        }
    }
}

@Composable
private fun Title(scrollProvider: () -> Int) {

    val scroll = scrollProvider()

    val maxOffset = with(LocalDensity.current) { maxTitleOffset.toPx() }
    val maxTitleHeight = with(LocalDensity.current) { titleHeight.toPx() }

    val titleOffset = (maxOffset - scroll).coerceAtLeast(0f)

    val minFontScale = with(LocalDensity.current) { minFontSize.toPx() / maxFontSize.toPx() }
    val fontScale = ((maxOffset - scroll)/maxOffset).coerceAtLeast(minFontScale)

    val maxPadding = with(LocalDensity.current) { maxTitlePadding.toPx() }
    val minPadding = with(LocalDensity.current) { minTitlePadding.toPx() }
    val titlePadding = ((maxOffset - scroll) * maxPadding / maxOffset).coerceAtLeast(minPadding)

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .heightIn(min = titleHeight)
            .fillMaxWidth()
            .statusBarsPadding()
            .offset {
                IntOffset(x = 0, y = titleOffset.toInt())
            }
    ) {
        Row {
             Column {
                 Text(
                     text = stringResource(id = R.string.todolist_screen_title),
                     fontSize = maxFontSize,
                     modifier = Modifier
                         .offset {
                             IntOffset(x = titlePadding.toInt(), y = 0)
                         }
                         .graphicsLayer {
                             scaleX = fontScale
                             scaleY = fontScale
                         }
                 )
                 Spacer(Modifier.height(4.dp))
                 if(maxOffset - scroll >  maxTitleHeight)
                     Text(
                         text = "Выполнено",
                         fontSize = 14.sp,
                         modifier = Modifier
                             .offset {
                                 IntOffset(x = titlePadding.toInt(), y = 0)
                             }
                             .graphicsLayer {
                                 scaleX = fontScale
                                 scaleY = fontScale
                             }
                     )
             }
            Spacer(Modifier.weight(1f))
            VisibilityCheckBox(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Bottom)
            )
            Spacer(Modifier.width(32.dp))
        }

    }
}

private fun CollapsingText() {

}

@Composable
private fun VisibilityCheckBox(modifier: Modifier = Modifier){
    var checked by remember { mutableStateOf(false) }
    ImageCheckBox(
        checked = checked,
        iconChecked = com.michel.core.ui.R.drawable.ic_visibility_off,
        iconUnchecked = com.michel.core.ui.R.drawable.ic_visibility_on,
        modifier = modifier
    ) {
        checked = it
    }
}


