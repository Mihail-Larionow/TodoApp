package com.michel.todoapp

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.feature.screens.navigation.TodoAppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme{
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TodoAppNavigation()
                }
            }
        }
    }
}

