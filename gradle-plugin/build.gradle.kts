plugins {
    kotlin("jvm") version "2.3.10"
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.3.0"
}

group = "com.seanshubin.code.structure"
version = "1.1.2"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.seanshubin.code.structure:code-structure-console:1.1.2")
}

gradlePlugin {
    website.set("https://github.com/SeanShubin/code-structure")
    vcsUrl.set("https://github.com/SeanShubin/code-structure.git")

    plugins {
        create("gradlePlugin") {
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
                name.set("Com Seanshubin Code Structure Gradle Plugin")
                description.set("Analyzes code structure and generates dependency reports to detect cycles, vertical dependencies, and maintain architectural consistency")
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
                    connection.set("scm:git:https://github.com/SeanShubin/code-structure.git")
                    developerConnection.set("scm:git:https://github.com/SeanShubin/code-structure.git")
                    url.set("https://github.com/SeanShubin/code-structure")
                }
            }
        }
    }
}
