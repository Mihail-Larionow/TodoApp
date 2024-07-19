plugins {
    id("android-features-convention")
    id("hilt-plugin")
    id("compose-plugin")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.michel.about"
}

dependencies {

    implementation(projects.core.ui)
    implementation(libs.yandex.div)
    implementation(libs.yandex.div.core)
    implementation(libs.div.json)
    implementation(libs.div.picasso)
    implementation(libs.androidx.ui.viewbinding)

}