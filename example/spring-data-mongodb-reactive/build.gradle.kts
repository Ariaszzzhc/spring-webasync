import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(plugin = "org.springframework.boot")

dependencies {
    implementation(project(":webasync-spring-boot-starter"))
    implementation("io.ktor:ktor-server-cio:1.2.4")
    implementation(spring("boot-starter-data-mongodb-reactive"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.3.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
