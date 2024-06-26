package com.michel.core.ui.custom

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.michel.core.ui.theme.TodoAppTheme
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeItem(
    modifier: Modifier = Modifier,
    onRemove: () -> Unit,
    onDone:(Boolean) -> Unit,
    content: @Composable () -> Unit
) {

    val swipeState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {
            val backgroundColor by animateColorAsState(
                targetValue = when(swipeState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> TodoAppTheme.color.green
                    SwipeToDismissBoxValue.EndToStart -> TodoAppTheme.color.red
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                },
                label = "Background color"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = backgroundColor
                    )
            ) {
                when(swipeState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        Icon(
                            painter = painterResource(com.michel.core.ui.R.drawable.ic_check),
                            contentDescription = stringResource(com.michel.core.ui.R.string.deleteContentDescription),
                            tint = TodoAppTheme.color.white,
                            modifier = Modifier
                                .size(TodoAppTheme.size.standardIcon)
                                .align(Alignment.CenterStart)
                                .padding(
                                    start = 16.dp
                                )
                        )
                    }
                    SwipeToDismissBoxValue.EndToStart -> {
                        Icon(
                            painter = painterResource(com.michel.core.ui.R.drawable.ic_delete),
                            contentDescription = stringResource(com.michel.core.ui.R.string.deleteContentDescription),
                            tint = TodoAppTheme.color.white,
                            modifier = Modifier
                                .size(TodoAppTheme.size.standardIcon)
                                .align(Alignment.CenterEnd)
                                .padding(
                                    end = 16.dp
                                )
                        )
                    }
                    SwipeToDismissBoxValue.Settled -> { }
                }

            }
        },
        modifier = modifier
            .animateContentSize()
    ) {
        content()
    }

    val coroutineScope = rememberCoroutineScope()
    when(swipeState.currentValue) {
        SwipeToDismissBoxValue.StartToEnd -> {
            LaunchedEffect(swipeState) {
                onDone(true)
                swipeState.snapTo(
                    targetValue = SwipeToDismissBoxValue.Settled
                )
            }
        }
        SwipeToDismissBoxValue.EndToStart -> {
            coroutineScope.launch {
                onRemove()
            }
        }
        SwipeToDismissBoxValue.Settled -> { }
    }
}