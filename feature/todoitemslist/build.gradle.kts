plugins {
    id("android-features-convention")
    id("hilt-plugin")
    id("compose-plugin")
}

android {
    namespace = "com.michel.todoitemslist"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.data)
}