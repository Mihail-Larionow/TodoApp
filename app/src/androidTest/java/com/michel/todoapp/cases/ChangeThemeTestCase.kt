package com.michel.todoapp.cases

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
 * Изменение выбора темы приложения
 *
 * Шаги:
 * 1. Открыть приложение
 * 2. Нажать «Авторизоваться через токен»
 * 3. Нажать «Настройки»
 * 4. Нажать «Темная / Светлая / Системная тема»
 *
 * Ожидаемый результат:
 * Выбранная кнопка выделена,
 * остальные кнопки не выделены.
 */
@HiltAndroidTest
@LargeTest
class ChangeThemeTestCase {

    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<TestActivity>()

    @Before
    fun setup() {
        hilt.inject()
    }

    @Test
    fun selectTheme(): Unit = runBlocking {

        setContent()

        clickOnElement("token_auth_button")
        clickOnElement("settings_button")
        clickOnElement("dark_theme_button")

        assertButtonIsSelected("dark_theme_button")
        assertButtonIsNotSelected("light_theme_button")
        assertButtonIsNotSelected("system_theme_button")

        clickOnElement("light_theme_button")

        assertButtonIsNotSelected("dark_theme_button")
        assertButtonIsSelected("light_theme_button")
        assertButtonIsNotSelected("system_theme_button")

        clickOnElement("system_theme_button")

        assertButtonIsNotSelected("dark_theme_button")
        assertButtonIsNotSelected("light_theme_button")
        assertButtonIsSelected("system_theme_button")

    }

    private fun setContent() {
        compose.setContent { TodoAppTheme { TodoAppNavigation() } }
    }

    private fun clickOnElement(testTag: String) {
        compose.onNodeWithTag(testTag, useUnmergedTree = true).performClick()
    }

    private fun assertButtonIsSelected(testTag: String) {
        assert(isSelected(testTag))
    }

    private fun assertButtonIsNotSelected(testTag: String) {
        assert(!isSelected(testTag))
    }

    private fun isSelected(testTag: String): Boolean {
        return compose.onNodeWithTag(testTag, useUnmergedTree = true)
            .fetchSemanticsNode()
            .config
            .getOrElse(SemanticsProperties.Selected) { false }
    }

}