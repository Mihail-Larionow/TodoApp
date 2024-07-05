package com.michel.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.todoapp.navigation.TodoAppNavigation
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity which is the entrypoint of the application
 */
@AndroidEntryPoint
class TodoAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }
}

@Composable
internal fun Content() {
    TodoAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            TodoAppNavigation()
        }
    }
}

