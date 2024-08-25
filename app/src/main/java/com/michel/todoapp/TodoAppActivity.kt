package com.michel.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.todoapp.navigation.TodoAppNavigation
import com.michel.todoapp.utils.TodoAppActivityState
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity which is the entrypoint of the application.
 */
@AndroidEntryPoint
class TodoAppActivity : ComponentActivity() {

    private val viewModel: TodoAppActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val screenState by viewModel.state.collectAsStateWithLifecycle()
            Content(screenState)
        }
    }
}

/**
 * Contains UI implementation of the main screen.
 */
@Composable
private fun Content(screenState: TodoAppActivityState) {
    val isDarkTheme = if (screenState.isSystemTheme) {
        isSystemInDarkTheme()
    } else {
        screenState.isDarkTheme
    }

    TodoAppTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            TodoAppNavigation()
        }
    }
}
