package com.michel.plugins.convention.tools

import org.gradle.api.JavaVersion

object AndroidConst {
    const val NAMESPACE = "com.michel.todoapp"
    const val COMPILE_SKD = 34
    const val MIN_SKD = 24
    const val TARGET_SDK = 34
    const val VERSION_CODE = 1
    const val VERSION_NAME = "1.0"
    val COMPILE_JDK_VERSION = JavaVersion.VERSION_1_8
    const val KOTLIN_JVM_TARGET = "1.8"
}