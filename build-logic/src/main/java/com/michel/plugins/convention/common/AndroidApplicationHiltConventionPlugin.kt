import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationHiltConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("com.google.dagger.hilt.android")
            }

            dependencies {
                add("ksp", versionCatalog.findLibrary("hilt.android.compiler").get())
                add("implementation", versionCatalog.findLibrary("hilt.android").get())
                add("implementation", versionCatalog.findLibrary("androidx.hilt.navigation.compose").get())
            }
        }
    }
}