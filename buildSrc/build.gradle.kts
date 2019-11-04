plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

gradlePlugin {
    plugins {
        this.register("optionalDependenciesPlugin") {
            id = "optional"
            implementationClass = "OptionalDependenciesPlugin"
        }
    }
}
