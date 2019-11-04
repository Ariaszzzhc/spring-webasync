import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.eclipse.model.EclipseModel

class OptionalDependenciesPlugin : Plugin<Project> {
    val OPTIONAL_CONFIGURATION_NAME = "optional"

    override fun apply(project: Project) {
        val optional = project.configurations.create("optional")
        project.plugins.withType(JavaPlugin::class.java) {
            val sourceSets = project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets
            sourceSets.all {
                compileClasspath = this.compileClasspath.plus(optional)
                runtimeClasspath = this.runtimeClasspath.plus(optional)
            }
        }

        project.plugins.withType(EclipsePlugin::class.java) {
            project.extensions.getByType(EclipseModel::class.java).classpath {
                plusConfigurations.add(optional)
            }
        }
    }
}