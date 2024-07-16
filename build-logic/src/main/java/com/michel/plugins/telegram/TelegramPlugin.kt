package com.michel.plugins.telegram

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.michel.plugins.telegram.tasks.SendApkDetailsTask
import com.michel.plugins.telegram.tasks.SendReportTask
import com.michel.plugins.telegram.tasks.ValidateApkSizeTask
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.register
import java.io.File

/**
 * Plugin that sends build reports through telegram bot
 */
class TelegramPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val androidComponents =
            project.extensions.findByType(AndroidComponentsExtension::class.java)
                ?: throw GradleException("Android plugin required.")
        val extension = project.extensions.create("telegram", TelegramExtension::class)

        val api = TelegramService(HttpClient(OkHttp))
        androidComponents.onVariants { variant ->

            val artifacts = variant.artifacts.get(SingleArtifact.APK)
            val file = File(getTempFilePath(project = project, variant = variant))

            val validateSizeTask = project.tasks.register(
                "validateApkSizeFor${variant.name.capitalized()}",
                ValidateApkSizeTask::class,
                api
            ).apply {
                val apkMaxSize = extension.apkMaxSize.getOrElse(Int.MAX_VALUE)
                val enabled = extension.validateSizeEnabled.getOrElse(false)

                configure {
                    maxSize.set(apkMaxSize)
                    apkDir.set(artifacts)
                    chatId.set(extension.telegramChatId)
                    token.set(extension.telegramToken)
                    output.set(file)
                    validateSizeEnabled.set(enabled)
                }
            }

            val apkDetailsTask = project.tasks.register(
                "tgDetailedReportFor${variant.name.capitalized()}",
                SendApkDetailsTask::class,
                api
            ).apply {
                val enabled = extension.apkDetailsEnabled.getOrElse(true)

                configure {
                    apkDir.set(artifacts)
                    chatId.set(extension.telegramChatId)
                    token.set(extension.telegramToken)
                    apkDetailsEnabled.set(enabled)
                }
            }

            project.tasks.register(
                "tgReportFor${variant.name.capitalized()}",
                SendReportTask::class,
                api
            ).apply {
                val android = project.extensions.findByType(AppExtension::class.java)
                    ?: throw GradleException("Android extension required.")

                val configVersionCode: Property<Int> = project.objects.property()
                configVersionCode.set(android.defaultConfig.versionCode ?: 0)

                configure {
                    apkDir.set(artifacts)
                    apkInfo.set(validateSizeTask.get().output)
                    variantName.set(variant.name)
                    versionCode.set(configVersionCode)
                    chatId.set(extension.telegramChatId)
                    token.set(extension.telegramToken)

                    finalizedBy(apkDetailsTask)
                }
            }
        }
    }

    private fun getTempFilePath(project: Project, variant: Variant): String {
        return project.layout.buildDirectory.get().asFile.absolutePath +
                "\\temp\\${variant.name}-build-info.txt"
    }

}