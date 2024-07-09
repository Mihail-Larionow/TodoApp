package com.michel.core.data.models

/**
 * Contains importance types
 */
sealed class Importance(val text: String) {
    data object High : Importance("!! Высокий")
    data object Basic : Importance("Нет")
    data object Low : Importance("Низкий")
}