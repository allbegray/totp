plugins {
    base
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
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

            // Sonatype/Maven Central publishing is handled by the Nexus publish plugin.
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
nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(providers.environmentVariable("SONATYPE_USERNAME"))
            password.set(providers.environmentVariable("SONATYPE_PASSWORD"))
        }
    }
}
