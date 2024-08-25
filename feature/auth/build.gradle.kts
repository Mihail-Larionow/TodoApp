plugins {
    id("android-features-convention")
    id("hilt-plugin")
    id("compose-plugin")
}

android {
    namespace = "com.michel.auth"
}

dependencies {
    api(projects.core.data)
    api(projects.core.ui)
}