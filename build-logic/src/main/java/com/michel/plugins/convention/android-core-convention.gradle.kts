import gradle.kotlin.dsl.accessors._a937729d8a1f305b163059a48f19fa79.implementation
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    baseAndroidConfig()
}

dependencies {

    testImplementation(libs.junit)
    implementation(libs.javax.inject)
    implementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}