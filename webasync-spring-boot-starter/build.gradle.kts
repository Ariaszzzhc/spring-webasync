import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    api(project(":webasync"))
    api(spring("boot-starter"))
    api("io.ktor:ktor-server-core:1.2.4")
    api("io.ktor:ktor-jackson:1.2.4")
    optional("io.ktor:ktor-server-netty:1.2.4")
    optional("io.ktor:ktor-server-cio:1.2.4")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
}
