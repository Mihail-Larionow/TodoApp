import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationRetrofitConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                add("implementation", versionCatalog.findLibrary("okhttp").get())
                add("implementation", versionCatalog.findLibrary("retrofit").get())
                add("implementation", versionCatalog.findLibrary("converter-gson").get())
                add("implementation", versionCatalog.findLibrary("logging-interceptor").get())
                add("implementation", versionCatalog.findLibrary("jetbrains-kotlinx-serialization-json").get())
            }
        }
    }
}