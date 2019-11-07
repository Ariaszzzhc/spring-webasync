import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(spring("web"))
    implementation(spring("core"))
    implementation(spring("beans"))
    api("io.ktor:ktor-server-cio:1.2.5")
//    api("io.ktor:ktor-http-cio-jvm:1.2.5")
//    api("io.ktor:ktor-network:1.2.5")
//    api("io.ktor:ktor-utils:1.2.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    optional(spring("context"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
}
