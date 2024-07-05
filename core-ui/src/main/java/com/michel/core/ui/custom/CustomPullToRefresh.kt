package com.michel.core.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.michel.core.ui.theme.TodoAppTheme

/**
 * Pull-to-refresh composable function that start do something after pull
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPullToRefreshItem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    Box(
        modifier = modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        content()

        ProgressBar(
            pullToRefreshState = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgressBar(
    modifier: Modifier = Modifier,
    pullToRefreshState: PullToRefreshState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            pullToRefreshState.startRefresh()
        } else {
            pullToRefreshState.endRefresh()
        }
    }

    PullToRefreshContainer(
        state = pullToRefreshState,
        containerColor = TodoAppTheme.color.elevated,
        contentColor = TodoAppTheme.color.blue,
        modifier = modifier
    )
}