plugins {
    base
}

allprojects {
    group = "io.github.allbegray"
    version = "1.0.0"

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    extensions.configure<org.gradle.api.publish.PublishingExtension> {
        publications {
            create<org.gradle.api.publish.maven.MavenPublication>("mavenJava") {
                from(components["java"])
                pom {
                    name.set(project.name)
                    description.set("TOTP utilities for the JVM.")
                    url.set("https://github.com/allbegray/totp")
                    licenses {
                        license {
                            name.set("Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                    developers {
                        developer {
                            id.set("allbegray")
                            name.set("allbegray")
                            url.set("https://github.com/allbegray")
                        }
                    }
                    scm {
                        connection.set("scm:git:https://github.com/allbegray/totp.git")
                        developerConnection.set("scm:git:ssh://git@github.com/allbegray/totp.git")
                        url.set("https://github.com/allbegray/totp")
                    }
                }
            }
        }

        repositories {
            val githubToken = System.getenv("GITHUB_TOKEN") ?: System.getenv("GH_TOKEN")
            if (githubToken != null) {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/allbegray/totp")
                    credentials {
                        username = System.getenv("GITHUB_ACTOR") ?: "allbegray"
                        password = githubToken
                    }
                }
            }

            val sonatypeUsername = System.getenv("SONATYPE_USERNAME") ?: System.getenv("MAVEN_CENTRAL_USERNAME")
            val sonatypePassword = System.getenv("SONATYPE_PASSWORD") ?: System.getenv("MAVEN_CENTRAL_PASSWORD")
            if (sonatypeUsername != null && sonatypePassword != null) {
                maven {
                    name = "Sonatype"
                    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
                        username = sonatypeUsername
                        password = sonatypePassword
                    }
                }
            }
        }
    }

    val publishingExt = extensions.getByType(org.gradle.api.publish.PublishingExtension::class.java)
    extensions.configure<org.gradle.plugins.signing.SigningExtension> {
        val signingKey = System.getenv("SIGNING_KEY")
        if (signingKey != null) {
            useInMemoryPgpKeys(signingKey, System.getenv("SIGNING_PASSWORD"))
            sign(publishingExt.publications["mavenJava"])
        }
    }
}