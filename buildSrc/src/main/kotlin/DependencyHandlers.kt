import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.optional(dependencyNotation: Any): Dependency? =
    add("optional", dependencyNotation)

fun spring(module: String) = "org.springframework:spring-$module:$SPRING_VERSION"
