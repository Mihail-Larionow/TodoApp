plugins {
    id("android-app-convention")
    id("hilt-plugin")
    id("compose-plugin")
    id("telegram-reporter")
}

telegram {
    // May be replaced by String (ex. val chatId = "your_chat_id")
    val chatId = providers.environmentVariable("TELEGRAM_CHAT_ID").get()
    telegramChatId = chatId

    // May be replaced by String (ex. val token = "your_token")
    val token = providers.environmentVariable("TELEGRAM_TOKEN").get()
    telegramToken = token

    // (OPTIONAL) May be replaced by Boolean (ex. validateSizeEnabled = true)
    validateSizeEnabled = true

    // (OPTIONAL) May be replaced by Int (ex. apkMaxSize = 1024)
    apkMaxSize = 15360

    // (OPTIONAL) May be replaced by Boolean (ex. apkDetailsEnabled = false)
    apkDetailsEnabled = true
}

android {
    defaultConfig {
        // May be replaced (ex. val yandexClientId = your_client_id)
        val yandexClientId = providers.environmentVariable("YANDEX_CLIENT_ID").get()
        manifestPlaceholders["YANDEX_CLIENT_ID"] = yandexClientId

        // May be replaced (ex. val clientId = your_bearer_token)
        val tokenBearer = providers.environmentVariable("TOKEN_BEARER").get()
        buildConfigField("String", "TOKEN_BEARER", "\"$tokenBearer\"")

        // May be replaced (ex. val tokenOAuth = your_oauth_token)
        val tokenOAuth = providers.environmentVariable("TOKEN_OAUTH").get()
        buildConfigField("String", "TOKEN_OAUTH", "\"$tokenOAuth\"")

        // May be replaced (ex. val baseUrl = www.google.com)
        val baseUrl = project.properties["BASE_URL"]
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.create("release").apply {
                storeFile = File("$projectDir/release/keystore/keys.jks")
                keyAlias = providers.environmentVariable("SIGNING_KEY_ALIAS").get()
                storePassword = providers.environmentVariable("SIGNING_STORE_PASSWORD").get()
                keyPassword = providers.environmentVariable("SIGNING_KEY_PASSWORD").get()
            }
        }
    }
}

dependencies {
    implementation(projects.feature.todoitemdetails)
    implementation(projects.feature.todoitemslist)
    implementation(projects.feature.todoworker)
    implementation(projects.feature.auth)
}

