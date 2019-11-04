import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.kotlin.plugin.allopen") version KOTLIN_VERSION
}

group = "com.hiarias"
version = "0.1.0"

repositories {
    mavenCentral()
    jcenter()
}

apply(plugin = "optional")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(spring("web"))
    implementation(spring("core"))
    implementation(spring("beans"))
    optional(spring("context"))
}

allOpen {
    annotation("kotlin.Experimental")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
