plugins {
    id("android-core-convention")
    id("hilt-plugin")
}

android {
    namespace = "com.michel.core.data"
}

dependencies {
    api(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.database)
}