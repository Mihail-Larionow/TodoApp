import gradle.kotlin.dsl.accessors._a937729d8a1f305b163059a48f19fa79.implementation
import gradle.kotlin.dsl.accessors._a937729d8a1f305b163059a48f19fa79.testImplementation

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    baseAndroidConfig()
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {

    // Yandex Auth
    implementation(libs.authsdk)

    // Worker
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.javax.inject)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}