package com.michel.plugins.telegram

import org.gradle.api.provider.Property

interface TelegramExtension {
    val apkMaxSize: Property<Int>
    val telegramChatId: Property<String>
    val telegramToken: Property<String>
    val validateSizeEnabled: Property<Boolean>
    val apkDetailsEnabled: Property<Boolean>
}