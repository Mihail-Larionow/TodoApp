plugins {
    id("android-features-convention")
    id("hilt-plugin")
    id("compose-plugin")
}

android {
    namespace = "com.michel.auth"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)

    // Yandex Auth
    implementation(libs.authsdk)
}