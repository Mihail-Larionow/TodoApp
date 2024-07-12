rootProject.name = "TodoApp"

include(":app")
include(":core:ui")
include(":core:network")
include(":core:data")
include(":feature:todoitemslist")
include(":feature:todoitemdetails")
include(":core:database")
include(":feature:auth")
include(":feature")
include(":core")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
