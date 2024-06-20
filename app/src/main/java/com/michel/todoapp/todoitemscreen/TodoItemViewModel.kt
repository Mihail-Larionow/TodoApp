package com.michel.todoapp.todoitemscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoItemViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    init{
        viewModelScope.launch {
            val id = savedStateHandle.get<String>("id") ?: "none"
            Log.i("app", id)
        }
    }

}