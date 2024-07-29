package com.michel.core.data.repository

import com.michel.common.utils.ErrorData
import com.michel.core.data.datasource.local.LocalDataSource
import com.michel.core.data.datasource.remote.RemoteDataSource
import com.michel.core.data.models.Importance
import com.michel.core.data.models.TodoItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TodoItemsRepositoryImplTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: TodoItemsRepository

    private val error: ErrorData = mockk()
    private val local: LocalDataSource = mockk()
    private val remote: RemoteDataSource = mockk()
    private val scope = TestScope(StandardTestDispatcher())

    @Test
    fun `should return item with id`() = runTest {
        repository = produceRepository()

        val todoItem = TodoItem(
            id = "1234567",
            text = "some text",
            importance = Importance.Basic,
            isDone = true,
            createdAt = 1,
        )
        val itemId = todoItem.id

        coEvery { local.getItem(itemId) } returns todoItem

        val expected = todoItem
        val actual = repository.getTodoItem(itemId)

        assertEquals(actual, expected)
    }

    @Test
    fun `should return items flow`() = runTest {
        repository = produceRepository()

        val itemsFlow = flowOf(
            listOf(
                TodoItem(
                    id = "1234567",
                    text = "some text",
                    importance = Importance.Basic,
                    isDone = true,
                    createdAt = 1,
                ),

                TodoItem(
                    id = "2345678",
                    text = "another text",
                    importance = Importance.High,
                    isDone = false,
                    createdAt = 1,
                )
            )
        )

        coEvery { local.getAllItemsFlow() } returns itemsFlow

        val expected = itemsFlow
        val actual = repository.getTodoItemsFlow()

        assertEquals(actual, expected)
    }

    private fun produceRepository(): TodoItemsRepositoryImpl = TodoItemsRepositoryImpl(
        local = local,
        remote = remote,
        error = error,
        appScope = scope,
    )

}