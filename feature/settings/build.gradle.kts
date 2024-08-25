plugins {
    id("android-features-convention")
    id("hilt-plugin")
    id("compose-plugin")
}

android {
    namespace = "com.michel.settings"
}

dependencies {
    api(projects.core.data)
    implementation(projects.core.ui)
}