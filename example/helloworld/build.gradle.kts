import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(plugin = "org.springframework.boot")

dependencies {
    implementation(project(":webasync-spring-boot-starter"))
    implementation("io.ktor:ktor-server-cio:1.2.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
}
