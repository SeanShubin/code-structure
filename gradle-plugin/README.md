# Code Structure Gradle Plugin

Gradle plugin wrapper for [code-structure](https://github.com/SeanShubin/code-structure).

## Prerequisites

The code-structure JAR must be available in your local Maven repository. From the parent directory:

```bash
mvn clean install
```

## Building the Plugin

```bash
cd gradle-plugin
./gradlew build
```

## Publishing

The Gradle plugin publishes to:

- **Maven Central** (via Central Portal - new way)
- **Gradle Plugin Portal**

See [PUBLISHING.md](PUBLISHING.md) for detailed setup and publishing instructions.

**Quick publish (from parent directory):**

```bash
mvn deploy
```

## Usage

### From Gradle Plugin Portal (after publishing)

In your project's `build.gradle.kts`:

```kotlin
plugins {
    id("com.seanshubin.code.structure") version "1.1.1"
}

codeStructure {
    configFile.set("code-structure-config.json")  // Optional, this is the default
}
```

Then run:

```bash
./gradlew analyzeCodeStructure
```

### From Maven Local (for testing)

```bash
# From gradle-plugin directory
./gradlew publishToMavenLocal
```

Then in your test project's `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```

## Configuration

The plugin expects a `code-structure-config.json` file at the project root (or specify a different path via
`configFile`). See the main [code-structure documentation](../README.md) for configuration details.

## Development Notes

This plugin is a thin wrapper that:

1. Declares dependency on `com.seanshubin.code.structure:code-structure-console:1.1.1`
2. Provides a Gradle task that invokes the existing CLI entry point
3. Maps Gradle configuration to code-structure configuration

The core code-structure logic remains in the Maven-built modules.

## Architecture

- **Maven builds:** Core code-structure modules (written in Kotlin)
- **Gradle builds:** This plugin (wrapper around the Maven-built JAR)
- **Maven coordinates publishing:** `mvn deploy` publishes both Maven modules and Gradle plugin
