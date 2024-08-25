plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("telegram-reporter") {
        id = "telegram-reporter"
        implementationClass = "com.michel.plugins.telegram.TelegramPlugin"
    }
    plugins.register("hilt-plugin") {
        id = "hilt-plugin"
        implementationClass = "AndroidApplicationHiltConventionPlugin"
    }
    plugins.register("compose-plugin") {
        id = "compose-plugin"
        implementationClass = "AndroidApplicationComposeConventionPlugin"
    }
    plugins.register("room-plugin") {
        id = "room-plugin"
        implementationClass = "AndroidApplicationRoomConventionPlugin"
    }
    plugins.register("retrofit-plugin") {
        id = "retrofit-plugin"
        implementationClass = "AndroidApplicationRetrofitConventionPlugin"
    }
}

dependencies {

    // Ktor
    implementation(libs.ktor.client)
    implementation(libs.ktor.client.okhttp)

    // Gradle plugins
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.gradle.plugin)

    implementation(libs.agp)
    implementation(libs.kotlin.coroutines.core)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

}