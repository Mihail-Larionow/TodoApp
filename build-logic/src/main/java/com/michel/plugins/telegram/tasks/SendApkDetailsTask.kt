package com.michel.plugins.telegram.tasks

import com.michel.plugins.telegram.TelegramService
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.FileNotFoundException
import java.util.zip.ZipFile
import javax.inject.Inject

abstract class SendApkDetailsTask @Inject constructor(
    private val api: TelegramService
) : DefaultTask() {

    @get:InputDirectory
    abstract val apkDir: DirectoryProperty

    @get:Input
    abstract val chatId: Property<String>

    @get:Input
    abstract val token: Property<String>

    @get:Input
    abstract val apkDetailsEnabled: Property<Boolean>

    @TaskAction
    fun execute() {
        if (!apkDetailsEnabled.get()) return

        val chatId = chatId.get()
        val token = token.get()

        val file = apkDir.get().asFile.listFiles()
            ?.firstOrNull { it.name.endsWith(".apk") }
            ?: throw FileNotFoundException("No apk file found")

        val zip = ZipFile(file)
        runBlocking {
            api.sendMessage(
                message = getSuccessMessage(zip),
                chatId = chatId,
                token = token
            ).apply {
                println(getSuccessMessage(zip))
                println("Status = $status")
            }
        }
    }

    private fun getSuccessMessage(zip: ZipFile): String {
        return zip.entries()
            .asSequence()
            .groupBy { it.name.split("/").first() }
            .asSequence()
            .map { it.key to it.value.sumOf { file -> file.size } / (1024.0 * 1024.0) }
            .sortedByDescending { it.second }
            .map { "`\\- ${it.first} ${"%.2f".format(it.second)} MB`" }
            .joinToString("\n")
    }

}
