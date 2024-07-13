plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    baseAndroidConfig()
    namespace = "com.michel.todoworker"
}

dependencies {

    // Worker
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.javax.inject)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}