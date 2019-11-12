import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    api("io.ktor:ktor-server-core:1.2.4")
    api(spring("web"))
    optional(spring("context"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
}
