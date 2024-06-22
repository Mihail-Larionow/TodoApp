package com.michel.todoapp

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.todoapp.navigation.TodoAppNavigation
import com.michel.todoapp.todolistscreen.TodoListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme{
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = TodoAppTheme.color.backPrimary
                        )
                ) {
                    TodoAppNavigation()
                }
            }
        }
    }
}

