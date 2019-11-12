plugins {
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.50"
}

subprojects {
    group = "com.hiarias"
    version = "0.1.0"

    repositories {
        mavenCentral()
        jcenter()
    }

    apply(plugin = "optional")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "kotlin-spring")
}
