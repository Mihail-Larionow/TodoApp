plugins {
    id("android-worker-convention")
    id("hilt-plugin")
}

dependencies {
    implementation(projects.core.data)
}