import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.kotlin.dsl.kotlin

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

configure<BaseAppModuleExtension> {
    baseAndroidConfig()
    buildFeatures {
        compose = true
        buildConfig = true
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

    // Testing
    implementation(libs.androidx.runner)
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockwebserver)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.mockwebserver)
    androidTestImplementation(libs.androidx.espresso.core)

}