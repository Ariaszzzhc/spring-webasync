plugins {
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.50"
    id("org.springframework.boot") version "2.2.1.RELEASE" apply false
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
