plugins {
    kotlin("jvm") version "2.3.10"
    `java-gradle-plugin`
    `maven-publish`
    signing
    id("com.gradle.plugin-publish") version "1.3.0"
}

group = "com.seanshubin.code.structure"
version = "1.1.1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.seanshubin.code.structure:code-structure-console:1.1.1")
}

gradlePlugin {
    website.set("https://github.com/SeanShubin/code-structure")
    vcsUrl.set("https://github.com/SeanShubin/code-structure.git")

    plugins {
        create("codeStructure") {
            id = "com.seanshubin.code.structure"
            implementationClass = "com.seanshubin.code.structure.gradle.CodeStructurePlugin"
            displayName = "Code Structure Plugin"
            description =
                "Analyzes code structure and generates dependency reports to detect cycles, vertical dependencies, and maintain architectural consistency"
            tags.set(listOf("code-quality", "architecture", "dependencies", "static-analysis"))
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("pluginMaven") {
            artifactId = "code-structure-gradle-plugin"

            pom {
                name.set("Code Structure Gradle Plugin")
                description.set("Gradle plugin for code structure analysis")
                url.set("https://github.com/SeanShubin/code-structure")

                licenses {
                    license {
                        name.set("The Unlicense")
                        url.set("https://unlicense.org/")
                    }
                }

                developers {
                    developer {
                        id.set("SeanShubin")
                        name.set("Sean Shubin")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/SeanShubin/code-structure.git")
                    developerConnection.set("scm:git:ssh://github.com/SeanShubin/code-structure.git")
                    url.set("https://github.com/SeanShubin/code-structure")
                }
            }
        }
    }

    // Note: Gradle plugins are published to Gradle Plugin Portal, not Maven Central
    // Maven Central publishing is optional and not needed for plugin consumption
    // repositories {
    //     maven {
    //         name = "central"
    //         url = uri("https://central.sonatype.com/api/v1/publisher/upload")
    //         credentials {
    //             username = project.findProperty("centralUsername")?.toString()
    //                 ?: System.getenv("CENTRAL_USERNAME")
    //             password = project.findProperty("centralPassword")?.toString()
    //                 ?: System.getenv("CENTRAL_PASSWORD")
    //         }
    //     }
    // }
}

// Signing not needed for Gradle Plugin Portal (they sign on their end)
// Only needed if publishing to Maven Central
// signing {
//     // Use the same GPG passphrase environment variable as Maven
//     val signingPassword = System.getenv("MAVEN_GPG_PASSPHRASE")
//     if (signingPassword != null) {
//         // Configure GPG to use the passphrase from environment
//         extra["signing.gnupg.executable"] = "gpg"
//         extra["signing.gnupg.useLegacyGpg"] = "false"
//         extra["signing.gnupg.keyName"] = "FF6963DA7AF0C98BB46BD32E1AF18E39B486EEDE"
//         extra["signing.gnupg.passphrase"] = signingPassword
//         useGpgCmd()
//     }
//     sign(publishing.publications)
// }
//
// tasks.withType<Sign>().configureEach {
//     onlyIf { gradle.taskGraph.hasTask("publish") || gradle.taskGraph.hasTask("publishPlugins") }
// }
