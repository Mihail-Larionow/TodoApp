import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.michel.todoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.michel.todoapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildFeatures {
            buildConfig = true
        }

        // May be replaced by String (ex. val clientId = "your_client_id")
        val clientId = (project.properties["CLIENT_ID"] ?: "none").toString()
        manifestPlaceholders["YANDEX_CLIENT_ID"] = clientId

        // May be replaced by String (ex. val clientId = "your_bearer_token")
        val tokenBearer = (project.properties["TOKEN_BEARER"] ?: "none").toString()
        buildConfigField("String", "TOKEN_BEARER", tokenBearer)

        // May be replaced by String (ex. val tokenOAuth = "your_oauth_token")
        val tokenOAuth = (project.properties["TOKEN_OAUTH"] ?: "none").toString()
        buildConfigField("String", "TOKEN_OAUTH", tokenOAuth)

        // May be replaced by String (ex. val baseUrl = "https://www.google.com/")
        val baseUrl = (project.properties["BASE_URL"] ?: "none").toString()
        buildConfigField("String", "BASE_URL", baseUrl)

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(project(":feature-todoitemscreen"))
    implementation(project(":feature-todolistscreen"))
    implementation(project(":feature-authscreen"))
    implementation(project(":core-ui"))

    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.authsdk)
    implementation(libs.androidx.work.runtime.ktx)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

