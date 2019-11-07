import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    api(project(":webasync"))
    api(spring("boot"))
    api("io.ktor:ktor-server-cio:1.2.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
}
