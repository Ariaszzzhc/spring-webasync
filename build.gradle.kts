plugins {
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.50"
    id("org.springframework.boot") version "2.2.1.RELEASE" apply false
    `maven-publish`
    signing
}

project("webasync") {
    description = "ktor & Spring integration"
}

project("webasync-spring-boot-starter") {
    description = "Starter for building Ktor application using Spring Framework support"
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
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    if (name in listOf("webasync", "webasync-spring-boot-starter")) {
        tasks.register<Jar>("sourcesJar") {
            from(sourceSets.main.get().allSource)
            archiveClassifier.set("sources")
        }

        tasks.register<Jar>("javadocJar") {
            from(tasks.javadoc)
            archiveClassifier.set("javadoc")
        }

        publishing {
            repositories {
                maven {
                    url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                    credentials {
                        username = project.properties["ossUsername"].toString()
                        password = project.properties["ossPassword"].toString()
                    }
                }
            }

            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])
                    artifact(tasks["sourcesJar"])
                    artifact(tasks["javadocJar"])

                    afterEvaluate {
                        artifactId = tasks.jar.get().archiveBaseName.get()
                    }

                    @Suppress("UnstableApiUsage")
                    pom {
                        name.set(project.name)
                        description.set(project.description)
                        url.set("https://github.com/Ariaszzzhc/spring-webasync")

                        licenses {
                            license {
                                name.set("Apache License Version 2.0")
                                url.set("http://www.apache.org/licenses/")
                            }
                        }

                        developers {
                            developer {
                                id.set("Ariaszzzhc")
                                name.set("Ariaszzzhc")
                                email.set("ariaszzzhc@qq.com")
                                url.set("https://www.hiarias.com")
                            }
                        }

                        scm {
                            connection.set("scm:git:git://github.com/Ariaszzzhc/spring-webasync")
                            developerConnection.set("scm:git:ssh://github.com/Ariaszzzhc/spring-webasync")
                            url.set("https://github.com/Ariaszzzhc/spring-webasync")
                        }
                    }
                }
            }
        }

        signing {
            sign(publishing.publications["mavenJava"])
        }
    }
}
