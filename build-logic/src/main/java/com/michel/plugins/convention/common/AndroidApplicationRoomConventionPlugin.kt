import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationRoomConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
            }

            dependencies {
                add("api", versionCatalog.findLibrary("androidx.room.runtime").get())
                add("ksp", versionCatalog.findLibrary("androidx.room.compiler").get())
                add("implementation", versionCatalog.findLibrary("androidx.room.ktx").get())
                add("annotationProcessor", versionCatalog.findLibrary("androidx.room.compiler").get())
            }
        }
    }
}