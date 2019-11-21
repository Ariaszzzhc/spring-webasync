import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    api("io.ktor:ktor-server-core:1.2.4")
    api(spring("web"))
    optional(spring("context"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testImplementation("io.ktor:ktor-server-test-host:1.2.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
}
