plugins {
    id("android-core-convention")
    id("hilt-plugin")
}

android {
    namespace = "com.michel.core.data"
}

dependencies {
    api(projects.core.common)
    api(projects.core.network)
    api(projects.core.database)
}