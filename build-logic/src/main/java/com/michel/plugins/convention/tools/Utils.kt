import com.android.build.gradle.BaseExtension
import com.michel.plugins.convention.tools.AndroidConst

fun BaseExtension.baseAndroidConfig() {

    namespace = AndroidConst.NAMESPACE
    setCompileSdkVersion(AndroidConst.COMPILE_SKD)

    defaultConfig {
        minSdk = AndroidConst.MIN_SKD
        targetSdk = AndroidConst.TARGET_SDK
        versionCode = AndroidConst.VERSION_CODE
        versionName = AndroidConst.VERSION_NAME

        testInstrumentationRunner = "com.michel.todoapp.Runner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = AndroidConst.COMPILE_JDK_VERSION
        targetCompatibility = AndroidConst.COMPILE_JDK_VERSION
    }

    kotlinOptions {
        jvmTarget = AndroidConst.KOTLIN_JVM_TARGET
    }

}