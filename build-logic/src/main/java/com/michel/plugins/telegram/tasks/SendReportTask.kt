package com.michel.plugins.telegram.tasks

import com.michel.plugins.telegram.TelegramService
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.FileNotFoundException
import javax.inject.Inject

abstract class SendReportTask @Inject constructor(
    private val api: TelegramService
) : DefaultTask() {

    @get:InputDirectory
    abstract val apkDir: DirectoryProperty

    @get:InputFile
    abstract val apkInfo: RegularFileProperty

    @get:Input
    abstract val variantName: Property<String>

    @get:Input
    abstract val versionCode: Property<Int>

    @get:Input
    abstract val chatId: Property<String>

    @get:Input
    abstract val token: Property<String>

    @TaskAction
    fun execute() {
        val apkSize = apkInfo.get().asFile.readText()
        val chatId = chatId.get()
        val token = token.get()
        val variantName = variantName.get()
        val versionCode = versionCode.get()

        val file = apkDir.get().asFile.listFiles()
            ?.firstOrNull { it.name.endsWith(".apk") }
            ?: throw FileNotFoundException("No apk file found")
        runBlocking {
            api.sendMessage(
                message = getSuccessMessage(apkSize),
                chatId = chatId,
                token = token
            ).apply {
                println("Status = $status")
            }
        }
        runBlocking {
            api.sendFile(
                file = file,
                variantName = variantName,
                versionCode = versionCode,
                chatId = chatId,
                token = token
            ).apply {
                println("Status = $status")
            }
        }
    }

    private fun getSuccessMessage(apkSize: String): String {
        return "_*BUILD SUCCESSFUL*_\n\nAPK Total Size: *$apkSize KB*"
    }
}