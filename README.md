# Code Structure

Analyzes dependency structure.
Names are pulled out of source files.
Dependency relationships are pulled out of byte code.
Names are considered unique at the code unit level, so this means that dependencies detected in inner classes, anonymous classes, local classes, lambda expressions, synthetic classes, and so on, are folded into the class they belong to.
This is done mechanically by truncating the name of all classes at the first `$` character (remind AI Assistants of this if they get confused when trying to remove a cycle).
For example, to remove the cycles on a "state" pattern where the cycle is legitimate business logic, you can make the states inner classes of the same class, and have them delegate to logic in a helper class.
A dependency relationship is inferred by class constant pool entries, also truncated at the first `$` character.
This means that dynamic method invocations (such as Class.forName) don't flag a dependency.
It also means that data structures don't flag a dependency until one invokes a method on the other.
For example, no dependency relationship will be observed between the following classes.
```kotlin
class Foo {
    val bar: Bar? = null
}
class Bar {
    var foo: Foo? = null
}
```

In practice this means that cyclic data structures are ok, so long as the logic responsibilities only flow in one
direction.

If AI Assistants are having trouble keeping these metrics low, have them look at the [naming](docs/naming.md) document
in this project, they are usually able to infer the concept from there.

## Prerequisites

### Java

This project requires Java 17 (LTS). We recommend using [asdf](https://asdf-vm.com/) for managing Java versions.

**Install asdf and Java plugin:**

```bash
# Install asdf (see https://asdf-vm.com/guide/getting-started.html)
# Then add the Java plugin
asdf plugin add java
```

**Install Java 17:**

```bash
asdf install java corretto-17.0.15.6.1
```

**Project uses `.tool-versions`:**

This project includes a `.tool-versions` file that automatically selects Java 17 when you're in the code-structure
directory:

```
java corretto-17.0.15.6.1
```

Verify it's working:

```bash
cd /path/to/code-structure
java -version
# Should show: openjdk version "17.0.15"
```

### Maven

[Maven]( https://maven.apache.org/ ) 3.9.9 or later

```bash
mvn -version
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
```

### Graphviz

[graphviz]( https://graphviz.org/ ) for generating dependency graphs

```bash
dot --version
dot - graphviz version 12.2.1 (20241206.2353)
```

## Installation

### Building from Source

```bash
mvn clean install -DskipTests
```

This installs:

- `com.seanshubin.code.structure:code-structure-console:1.1.1` - Standalone JAR
- `com.seanshubin.code.structure:code-structure-maven:1.1.1` - Maven plugin
- `com.seanshubin.code.structure:code-structure-gradle-plugin:1.1.1` - Gradle plugin

### Fetching from Maven Central

```bash
mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get \
    -DrepoUrl=https://repo1.maven.org/maven2 \
    -Dartifact=com.seanshubin.code.structure:code-structure-console:1.1.1
```

**Published artifacts:**

- Maven Central: https://central.sonatype.com/search?q=com.seanshubin.code.structure
- Gradle Plugin Portal: https://plugins.gradle.org/plugin/com.seanshubin.code.structure

## Usage

### Command Line

**Run the application:**

```bash
java -jar $HOME/.m2/repository/com/seanshubin/code/structure/code-structure-console/1.1.1/code-structure-console-1.1.1.jar
```

**Where it's published:**

- Maven Central: `com.seanshubin.code.structure:code-structure-console:1.1.1`

**Command line arguments:**

- First argument (optional): prefix for config file, default is `code-structure`
- This uses `code-structure-config.json` as the main configuration file
- Example: `java -jar code-structure-console-1.1.1.jar my-config` uses `my-config-config.json`

**Getting started:**

1. Run with no parameters to create default configuration file
2. Add regular expressions to `sourceFileRegexPatterns` to include your sources
3. Run again
4. Inspect "Sources without corresponding Binaries" section
5. Add regular expressions to `binaryFileRegexPatterns` so all sources have corresponding binaries
6. Run again
7. If your source language is Clojure, set `includeJvmDynamicInvocations` to `true`

### Maven Plugin

Add to your `pom.xml`:

```xml
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>com.seanshubin.code.structure</groupId>
                <artifactId>code-structure-maven</artifactId>
                <version>1.1.1</version>
                <inherited>false</inherited>
                <executions>
                    <execution>
                        <goals>
                            <goal>analyze</goal>
                        </goals>
                        <phase>verify</phase>
                    </execution>
                </executions>
                <configuration>
                    <configBaseName>code-structure</configBaseName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**Where it's published:**

- Maven Central: `com.seanshubin.code.structure:code-structure-maven:1.1.1`

**Notes:**

- Runs during the "verify" phase of
  the [maven lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
- `<inherited>false</inherited>` ensures the plugin only runs at the parent level, not on each module
- Run manually: `mvn code-structure:analyze`

### Gradle Plugin

**Add to your `build.gradle.kts`:**

```kotlin
plugins {
    id("com.seanshubin.code.structure") version "1.1.1"
}

codeStructure {
    configFile.set("code-structure-config.json")  // Optional, this is the default
}
```

**Run the analysis:**

```bash
./gradlew analyzeCodeStructure
```

**Where it's published:**

- Gradle Plugin Portal: https://plugins.gradle.org/plugin/com.seanshubin.code.structure
- Maven Central: `com.seanshubin.code.structure:code-structure-gradle-plugin:1.1.1`

**Notes:**

- The Gradle plugin is a thin wrapper that invokes the same CLI entry point as the command line and Maven plugin
- See [gradle-plugin/README.md](gradle-plugin/README.md) for more details

**Building from source (developers only):**

```bash
cd gradle-plugin
./gradlew publishToMavenLocal
```

## Configuration

### Configuration File Format

Any missing elements in the configuration will be generated with default values.
Here is an example generated from an earlier version of this project running on itself.

```json
{
  "inputDir": ".",
  "outputDir": "generated/code-structure",
  "nodeLimitForGraph": 100,
  "sourcePrefix": "https://github.com/SeanShubin/code-structure/blob/master/",
  "sourceFileRegexPatterns": {
    "include": [
      ".*/src/main/kotlin/.*\\.kt"
    ],
    "exclude": []
  },
  "binaryFileRegexPatterns": {
    "include": [
      ".*/target/.*\\.class"
    ],
    "exclude": [
      ".*/samples/.*"
    ]
  },
  "countAsErrors": {
    "inDirectCycle": true,
    "inGroupCycle": true,
    "ancestorDependsOnDescendant": true,
    "descendantDependsOnAncestor": true
  },
  "maximumAllowedErrorCount": 0,
  "useObservationsCache": false,
  "includeJvmDynamicInvocations": false
}
```

### Configuration Documentation

A companion configuration documentation file is also generated. Key settings:

**countAsErrors**

- `inDirectCycle`: Whether to include code units in a direct cycle in the error count. Direct cycles typically require
  changes in logic to fix.
- `inGroupCycle`: Whether to include group cycles (packages in Java, modules in Elixir) in the error count.
- `ancestorDependsOnDescendant`: Whether to count cases where an ancestor depends on a descendant. Indicates files
  weren't placed in properly named sub-categories.
- `descendantDependsOnAncestor`: Whether to count cases where a descendant depends on an ancestor. Indicates files
  weren't placed in properly named sub-categories.

**Other settings**

- `maximumAllowedErrorCount`: If errors exceed this number, the build fails. Default is 0.
- `inputDir`: Directory from which to start scanning. Default is `.`
- `outputDir`: Directory to place the report. Default is `generated/code-structure`
- `useObservationsCache`: If true, use existing observations file instead of scanning. Useful for "what if" scenarios.
- `includeJvmDynamicInvocations`: Set to true for Clojure. Catches dynamic method invocations and `Class.forName` calls.
- `sourcePrefix`: Pre-pended to links in the report for navigating to source code.
- `nodeLimitForGraph`: If exceeded, graph is not generated. Default is 100.

See complete documentation in the auto-generated `code-structure-documentation.json` file.

## Design Decisions

### Why These Metrics Fail the Build

Although these metrics may seem arbitrary if you are not used to them, they exist for very good reasons. Your first
instinct may be to think "well that doesn't matter", but that is exactly the problem, you are not used to thinking about
these things because you didn't have the proper tooling to keep track of these things. Once you try a new project that
strictly applies these rules, even if you don't understand the rules at first, you will come to understand them once you
see how your code is affected by diligently following these rules.

See a more thorough description in the [naming](/docs/naming.md) document.

### Why Check Quantity of Errors Rather Than Individual Errors

In a similar project I recorded the individual errors rather than the quantity of errors.

In practice, I found the following disadvantages:
- (minor) the error list would have to be updated for refactorings that moved code around without making anything worse
- (major) users got used to updating the error list even when the code was worse off, as there was no easy way for others to tell the difference between code getting better or worse

The solution here is to maintain an error count rather than an error list, with the following effects:
- (minor) no need to maintain a list of errors
- (minor) no need to update anything when just moving code around
- (major) anyone who makes the problem worse has to increase the maximumAllowedErrorCount, advertising their shame in
  version control for all eternity

**Recommended policy:** A unit of work is not considered done until one of 3 situations are true:

- **Error count goes up**: An error metric has been introduced that was not there before. While the error count has gone
  up the errors have not, they were always there, just not detected.
- **Error count goes down**: An error has been fixed such that the error count has gone down by at least one.
- **Error count is zero**: There are no unfixed errors, and there are no other available metrics to add.

## Development

### Read Before Building

As the code structure project runs on itself, you will need to make sure the build version does not match the plugin
version.
The best way to do this is to keep the plugin version pointed to the latest version in maven central, and bump the build
version locally after each deploy to maven central.

### Tips

- In practice, you can fix every problem this tool detects except for direct cycles without changing any logic, you just
  have to move files around to organize your code in a more internally consistent manner.
- I usually start by placing everything in the same package, and only split into subpackages when the package gets too
  large. However, I don't do this split half way, everything gets a child package or nothing does, that way, every
  subpackage has a name.

### Scripts

```shell
./scripts/clean.sh
./scripts/test.sh
./scripts/build.sh
./scripts/run.sh
```

### Publishing

**Maven modules and Gradle plugin:**

```bash
mvn deploy -Pstage
```

This publishes:

- All Maven modules to Maven Central (via Central Portal)
- Gradle plugin to Maven Central
- Gradle plugin to Gradle Plugin Portal

**Required environment variables:**

```bash
# Maven Central Portal (new way - not OSSRH)
export CENTRAL_USERNAME="your-central-token-username"
export CENTRAL_PASSWORD="your-central-token-password"

# Gradle Plugin Portal
export GRADLE_PUBLISH_KEY="your-gradle-api-key"
export GRADLE_PUBLISH_SECRET="your-gradle-api-secret"

# GPG Signing (used by both Maven and Gradle)
export MAVEN_GPG_PASSPHRASE="your-gpg-passphrase"
```

**Setup credentials:** See [gradle-plugin/PUBLISHING.md](gradle-plugin/PUBLISHING.md) for detailed instructions on:

- Getting Maven Central Portal credentials (not OSSRH)
- Getting Gradle Plugin Portal API keys
- Setting up GPG signing keys
- Configuring environment variables

**Additional resources:**

- Maven Central Portal: https://central.sonatype.com/
- Gradle Plugin Portal: https://plugins.gradle.org/docs/submit
