package com.michel.plugins.telegram

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import java.io.File

private const val BASE_URL = "https://api.telegram.org"
private const val PARSE_MODE = "MarkdownV2"

class TelegramService(
    private val httpclient: HttpClient,
) {

    suspend fun sendMessage(message: String, chatId: String, token: String): HttpResponse {
        return httpclient.post("$BASE_URL/bot$token/sendMessage") {
            parameter("chat_id", chatId)
            parameter("text", message)
            parameter("parse_mode", PARSE_MODE)
        }
    }

    suspend fun sendFile(
        file: File,
        variantName: String,
        versionCode: Int,
        chatId: String,
        token: String
    ): HttpResponse {
        return httpclient.post("$BASE_URL/bot$token/sendDocument") {
            parameter("chat_id", chatId)
            setBody(MultiPartFormDataContent(getData(file, variantName, versionCode)))
        }
    }

    private fun getData(file: File, variantName: String, versionCode: Int): List<PartData> {
        return formData {
            append(
                key = "document",
                value = file.readBytes(),
                headers = getHeaders(variantName, versionCode)
            )
        }
    }

    private fun getHeaders(variantName: String, versionCode: Int): Headers {
        return Headers.build {
            append(
                name = HttpHeaders.ContentDisposition,
                value = "filename=todolist-$variantName-$versionCode.apk"
            )
        }
    }

}
