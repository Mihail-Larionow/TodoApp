package com.michel.todoapp.cases

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.LargeTest
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.todoapp.test.TestActivity
import com.michel.todoapp.navigation.TodoAppNavigation
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Кейс:
 * Добавление новой задачи
 *
 * Шаги:
 * 1. Открыть приложение
 * 2. Нажать «Авторизоваться через токен»
 * 3. Нажать на «+»
 * 4. Ввести описание задачи
 * 5. Нажать «Сохранить»
 *
 * Ожидаемый результат:
 * На экране «Мои дела» появилась новая задача первой в списке.
 */
@HiltAndroidTest
@LargeTest
class AddTodoItemCaseTest {

    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<TestActivity>()

    @Before
    fun setup() {
        hilt.inject()
    }

    @Test
    fun successfullyAddTodoItem(): Unit = runBlocking {

        val performedText = "some test text"

        setContent()
        clickOnElement("token_auth_button")
        clickOnElement("add_task_button")
        setText(text = performedText, testTag = "task_text_field")
        clickOnElement("save_task_button")
        assertItemAddedFirst(text = performedText, testTag = "todo_item_text")

    }

    private fun setContent() {
        compose.setContent { TodoAppTheme { TodoAppNavigation() } }
    }

    private fun clickOnElement(testTag: String) {
        compose.onNodeWithTag(testTag, useUnmergedTree = true).performClick()
    }

    private fun setText(text: String, testTag: String) {
        compose.onNodeWithTag(testTag, useUnmergedTree = true).performTextInput(text)
    }

    private fun assertItemAddedFirst(text: String, testTag: String) {
        compose.onAllNodesWithTag(testTag, useUnmergedTree = true)
            .onFirst()
            .assertTextEquals(text)
    }

}