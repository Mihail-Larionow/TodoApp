package com.michel.feature.todolistscreen

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.michel.common.utils.ErrorData
import com.michel.common.utils.NetworkState
import com.michel.core.data.models.TodoItem
import com.michel.core.data.repository.TodoItemsRepository
import com.michel.core.ui.viewmodel.ScreenIntent
import com.michel.core.ui.viewmodel.ViewModelBase
import com.michel.feature.todolistscreen.utils.ListScreenEffect
import com.michel.feature.todolistscreen.utils.ListScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenIntent.ToItemScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

/**
 * ViewModel for items list screen.
 */
@HiltViewModel
internal class TodoListScreenViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
    private val errorData: ErrorData,
    private val network: NetworkState,
) : ViewModelBase<ListScreenState, ListScreenIntent, ListScreenEffect>(ListScreenState()) {

    private var isFirstException = true

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    init {
        startDataObserving()
        startErrorsObserving()
        startConnectionObserving()
    }

    /**
     * Handles intents from the screen.
     */
    override fun handleIntent(intent: ScreenIntent) {
        when (intent) {
            ListScreenIntent.GetItemsIntent -> syncItems()
            ListScreenIntent.StartLoadingIntent -> startRefreshing()
            is ListScreenIntent.ChangeVisibilityIntent -> updateVisibility(intent.isNotVisible)
            is ListScreenIntent.DeleteItemIntent -> deleteItem(intent.item)
            is ListScreenIntent.UpdateItemIntent -> updateItem(intent.item)
            is ListScreenIntent.UpdateItemsIntent -> syncItems()
            is ToItemScreenIntent -> leaveToItemScreen(intent.id)
            ListScreenIntent.ToSettingsScreenIntent -> leaveToSettingsScreen()
        }
    }

    /**
     * Starts observing data from the local database.
     */
    private fun startDataObserving() {
        scope.launch {
            combine(state, repository.getTodoItemsFlow()) { state, list ->
                ListScreenState(
                    doneItemsHide = state.doneItemsHide,
                    doneItemsCount = list.count { it.isDone },
                    todoItems = list,
                    isRefreshing = state.isRefreshing,
                    isConnected = state.isConnected,
                )
            }.collect {
                setState { it }
            }
        }
    }

    /**
     * Starts observing exceptions of the application.
     */
    private fun startErrorsObserving() {
        scope.launch {
            errorData.errors.collectLatest { throwable ->
                if (isFirstException) {
                    showSnackBar("${throwable.message}")
                    isFirstException = currentState.isConnected
                }
            }
        }
    }

    /**
     * Starts observing connection state of the application.
     */
    private fun startConnectionObserving() {
        scope.launch {
            network.state.collect {
                if (it) {
                    isFirstException = true
                    startRefreshing()
                }
                setState { copy(isConnected = it) }
            }
        }
    }

    /**
     * Sets refreshing state.
     */
    private fun startRefreshing() {
        setState { copy(isRefreshing = true) }
    }

    /**
     * Changes checkbox state.
     *
     * @param isNotVisible - boolean field where true means
     * done items aren't visible and false - are visible.
     */
    private fun updateVisibility(isNotVisible: Boolean) {
        setState { copy(doneItemsHide = isNotVisible) }
    }

    /**
     * Synchronizes data in local database
     * by the information from the Internet.
     */
    private fun syncItems() {
        scope.launch(Dispatchers.IO) {
            setState { copy(failed = false) }
            val result = repository.synchronizeDataWithResult()
            result.onSuccess { onLoadSuccess() }
            result.onFailure { throwable -> onLoadFailure("${throwable.message}") }
        }
    }

    /**
     * Updates one item.
     *
     * @param updatedItem - the item that need to be updated.
     */
    private fun updateItem(updatedItem: TodoItem) {
        repository.updateTodoItem(updatedItem)
    }

    /**
     * Deletes one item.
     *
     * @param deletedItem - the item that need to be deleted.
     */
    private fun deleteItem(deletedItem: TodoItem) {
        repository.deleteTodoItem(deletedItem)
    }

    /**
     * Starts side effect that navigates to the detail screen.
     *
     * @param itemId - id of the needed item.
     */
    private fun leaveToItemScreen(itemId: String) {
        setEffect { ListScreenEffect.LeaveScreenToItemEffect(itemId) }
    }

    /**
     * Starts side effect that navigates to the settings screen.
     */
    private fun leaveToSettingsScreen() {
        setEffect { ListScreenEffect.LeaveScreenToSettingsEffect }
    }

    /**
     * Starts side effect that shows snack bar with message.
     *
     * @param message - the message needed to show.
     */
    private fun showSnackBar(message: String) {
        setEffect { ListScreenEffect.ShowSimpleSnackBarEffect(message) }
    }

    /**
     * Starts on load failure.
     *
     * @param message - the message needed to show.
     */
    private fun onLoadFailure(message: String) {
        setState {
            copy(
                isRefreshing = false,
                failed = true,
                errorMessage = message
            )
        }
        setEffect { ListScreenEffect.ShowButtonSnackBarEffect(message, "ПОВТОРИТЬ") }
    }

    /**
     * Starts on load success.
     */
    private fun onLoadSuccess() {
        setState {
            copy(
                isRefreshing = false,
                failed = false
            )
        }
    }

}