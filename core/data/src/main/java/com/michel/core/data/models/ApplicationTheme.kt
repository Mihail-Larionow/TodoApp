package com.michel.core.data.models

/**
 * Contains importance types
 */
sealed class ApplicationTheme(val name: String) {
    data object Light : ApplicationTheme("Light")
    data object Dark : ApplicationTheme("Dark")
    data object System : ApplicationTheme("System")
}