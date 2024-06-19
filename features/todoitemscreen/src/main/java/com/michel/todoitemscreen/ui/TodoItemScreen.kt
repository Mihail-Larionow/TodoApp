package com.michel.todoitemscreen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.michel.core.data.Priority

@Preview(showBackground = true)
@Composable
fun TodoItemScreen() {
    Box {
        Body()
        Header()
    }
}

@Composable
fun Header() {
    Row(Modifier.fillMaxWidth()){

    }
}

@Composable
fun Body() {
    Column(
        Modifier
        .fillMaxWidth()
    ) {
        TodoTextField(Modifier.fillMaxWidth())
        TodoPriority()
        TodoDeadline()
        DeleteButton()
    }
}

@Composable
fun TodoTextField(modifier: Modifier = Modifier) {
    var text by rememberSaveable {
        mutableStateOf("Введите текст")
    }

    TextField(
        value = text,
        onValueChange = {
            text = it
        },
        modifier = modifier
    )
}

@Composable
fun TodoPriority(
    priority: Priority = Priority.STANDARD,
    modifier: Modifier = Modifier
) {

}

@Composable
fun TodoDeadline(
    modifier: Modifier = Modifier
) {

}

@Composable
fun DeleteButton(
    modifier: Modifier = Modifier
) {

}
