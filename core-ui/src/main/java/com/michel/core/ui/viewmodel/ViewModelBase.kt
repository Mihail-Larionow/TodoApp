package com.michel.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class ViewModelBase<S : ScreenState, I : ScreenIntent, E : ScreenEffect>(
    initialState: S
) :
    ViewModel() {

    protected val currentState
        get() = state.value

    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _intent: MutableSharedFlow<I> = MutableSharedFlow()
    val intent = _intent.asSharedFlow()

    private val _effect: Channel<E> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            intent.collect {
                handleIntent(it)
            }
        }
    }

    fun setIntent(intent: I) {
        val newEvent = intent
        viewModelScope.launch {
            _intent.emit(newEvent)
        }
    }

    protected fun setState(state: S.() -> S) {
        val newState = currentState.state()
        _state.update {
            newState
        }
    }

    protected fun setEffect(effect: () -> E) {
        val effectValue = effect()
        viewModelScope.launch {
            _effect.send(effectValue)
        }
    }

    abstract fun handleIntent(intent: ScreenIntent)
}