package com.michel.plugins.telegram.tasks

import com.michel.plugins.telegram.TelegramService
import kotlinx.coroutines.runBlocking
import okio.FileNotFoundException
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class ValidateApkSizeTask @Inject constructor(
    private val api: TelegramService
) : DefaultTask() {

    @get:Input
    abstract val maxSize: Property<Int>

    @get:InputDirectory
    abstract val apkDir: DirectoryProperty

    @get:Input
    abstract val chatId: Property<String>

    @get:Input
    abstract val token: Property<String>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:Input
    abstract val validateSizeEnabled: Property<Boolean>

    @TaskAction
    fun execute() {
        val maxSize = maxSize.get()
        val chatId = chatId.get()
        val token = token.get()
        val file = apkDir.get().asFile
            .listFiles()
            ?.firstOrNull { it.name.endsWith(".apk") }
            ?: throw FileNotFoundException("No apk file found")

        val currentSize = file.length() / 1024
        if (currentSize > maxSize && validateSizeEnabled.get()) {
            runBlocking {
                api.sendMessage(
                    message = getErrorMessage(currentSize, maxSize.toLong()),
                    chatId = chatId,
                    token = token
                ).apply {
                    println("Status = $status")
                }
            }
            throw Exception("Apk file is too large.")
        } else {
            output.get().asFile.writeText(currentSize.toString())
        }
    }

    private fun getErrorMessage(currentSize: Long, maxSize: Long): String {
        return "_*BUILD FAILED*_" +
                "\n\nAPK total size: *${currentSize} KB*" +
                "\nAPK allowable size: *${maxSize} KB*"
    }

}