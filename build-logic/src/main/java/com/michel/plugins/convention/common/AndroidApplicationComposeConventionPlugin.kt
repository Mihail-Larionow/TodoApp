import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationComposeConventionPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            dependencies {
                val bom = versionCatalog.findLibrary("androidx.compose.bom").get()

                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))
                add("implementation", versionCatalog.findLibrary("androidx-ui").get())
                add("implementation", versionCatalog.findLibrary("androidx-ui-graphics").get())
                add("debugImplementation", versionCatalog.findLibrary("androidx-material3").get())
                add("debugImplementation", versionCatalog.findLibrary("androidx-ui-tooling").get())
                add("implementation", versionCatalog.findLibrary("androidx-activity-compose").get())
                add("implementation", versionCatalog.findLibrary("androidx-material3-android").get())
                add("implementation", versionCatalog.findLibrary("androidx-navigation-compose").get())
                add("implementation", versionCatalog.findLibrary("androidx-ui-tooling-preview").get())
                add("androidTestImplementation", versionCatalog.findLibrary("androidx-ui-test-junit4").get())
                add("debugImplementation", versionCatalog.findLibrary("androidx-ui-test-manifest").get())
                add("implementation", versionCatalog.findLibrary("androidx-lifecycle-viewmodel-compose").get())
                add("implementation", versionCatalog.findLibrary("androidx-lifecycle-runtime-compose-android").get())
            }
        }
    }
}