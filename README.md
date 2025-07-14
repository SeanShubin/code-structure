# Code Structure

## Tips
- In practice, you can fix every problem this tool detects except for direct cycles without changing any logic, you just have to move files around to organize your code in a more internally consistent manner.
- I usually start by placing everything in the same package, and only split into subpackages when the package gets too large.  However, I don't do this split half way, everything gets a child package or nothing does, that way, every subpackage has a name. 

## Getting started

- Check the "Prerequisites" section
- Install, either by building from source or fetching it from a maven repo url
  - Build from source `./scripts/clean-install-skip-tests.sh`
  - Fetch from a repo at MAVEN_SNAPSHOT_URL `./scripts/fetch-from-maven-repo-url.sh` 
- Run on your project with no parameters, this will create the default configuration file
  - java -jar $HOME/.m2/repository/com/seanshubin/code/structure/code-structure-console/1.0.3/code-structure-console-1.0.3.jar
  - If you need multiple configuration files, specify the base name of the configuration file as the first parameter
- Add regular expressions to the sourceFileRegexPatterns sections in the default configuration file so that all of your sources are included (see examples)
  - Run the application again
- Inspect the "Sources without corresponding Binaries" section of the generated report
  - Add regular expressions to the binaryFileRegexPatterns section so that all of your source files have corresponding binaries
  - Run the application again
- If your source language is Clojure, set includeJvmDynamicInvocations to true

## Command Line Arguments
- one argument, the prefix for the config file, default is `code-structure`
- this causes it to use `code-structure-config.json` as the main configuration file

## Configuration Explained

Any missing elements in the configuration will be generated with default values, including the documentation.
There is a generated "documentation" element in the configuration, which explains each configuration element.
Here is an example generated from an earlier version of this project running on itself.

```json
{
  "inputDir" : ".",
  "outputDir" : "generated/self",
  "nodeLimitForGraph" : 100,
  "sourcePrefix" : "https://github.com/SeanShubin/code-structure/blob/master/",
  "sourceFileRegexPatterns" : {
    "include" : [
      ".*/src/main/kotlin/.*\\.kt$"
    ],
    "exclude" : [
      "generated/.*"
    ]
  },
  "binaryFileRegexPatterns" : {
    "include" : [
      ".*/target/classes/.*\\.class"
    ],
    "exclude" : [
      "generated/.*"
    ]
  },
  "countAsErrors" : {
    "inDirectCycle" : true,
    "inGroupCycle" : true,
    "ancestorDependsOnDescendant" : true,
    "descendantDependsOnAncestor" : true
  },
  "maximumAllowedErrorCount" : 0,
  "useObservationsCache" : false,
  "includeJvmDynamicInvocations" : false,
  "documentation" : {
    "countAsErrors" : {
      "inDirectCycle" : [
        "path: countAsErrors.inDirectCycle",
        "default value: true",
        "default value type: Boolean",
        "Whether to include the number of code units in a direct cycle in the error count",
        "Direct cycles typically require changes in logic to fix, so they are riskier than the other metrics",
        "Set this to false if you want to focus on metrics that are easier to fix first"
      ],
      "inGroupCycle" : [
        "path: countAsErrors.inGroupCycle",
        "default value: true",
        "default value type: Boolean",
        "Whether to include the number of group cycles in the error count",
        "Groups are packages in java, modules in elixir"
      ],
      "ancestorDependsOnDescendant" : [
        "path: countAsErrors.ancestorDependsOnDescendant",
        "default value: true",
        "default value type: Boolean",
        "Whether to include the number cases where an ancestor depends on a descendant in error count",
        "Dependencies between super-categories and sub-categories indicate that files in the super-category weren't placed in a properly named sub-category",
        "Instead, organize your directory structure such that each directory either only contains other directories following the same rules, or only contains files"
      ],
      "descendantDependsOnAncestor" : [
        "path: countAsErrors.descendantDependsOnAncestor",
        "default value: true",
        "default value type: Boolean",
        "Whether to include the number cases where a descendant depends on a ancestor in error count",
        "Dependencies between super-categories and sub-categories indicate that files in the super-category weren't placed in a properly named sub-category",
        "Instead, organize your directory structure such that each directory either only contains other directories following the same rules, or only contains files"
      ]
    },
    "maximumAllowedErrorCount" : [
      "path: maximumAllowedErrorCount",
      "default value: 0",
      "default value type: Integer",
      "if the number of errors exceeds this number, the build will fail"
    ],
    "inputDir" : [
      "path: inputDir",
      "default value: .",
      "default value type: String",
      "the directory from which to start scanning"
    ],
    "outputDir" : [
      "path: outputDir",
      "default value: generated/code-structure",
      "default value type: String",
      "the directory to place the report"
    ],
    "useObservationsCache" : [
      "path: useObservationsCache",
      "default value: false",
      "default value type: Boolean",
      "if the observations file exists, use that instead of scanning the sources and binaries",
      "this is useful if you want to run 'what if' scenarios by manually changing the observations file"
    ],
    "includeJvmDynamicInvocations" : [
      "path: includeJvmDynamicInvocations",
      "default value: false",
      "default value type: Boolean",
      "Clojure invokes methods dynamically, so the class dependency won't show up as a class in the constant pool (CONSTANT_Class - 7)",
      "However, the class name will still show up as a string, so we can get it as a string in the constant pool (CONSTANT_Utf8 - 1)",
      "Reading the string constants instead of class constants will also catch instances of Class.forName, but only if the completed string exists in the constant pool, it will not be able to detect it in cases where the string is constructed at runtime."
    ],
    "sourcePrefix" : [
      "path: sourcePrefix",
      "default value: prefix for link to source code",
      "default value type: String",
      "pre-pended to links in the report, so you can navigate directly to the source code from the report"
    ],
    "sourceFileRegexPatterns" : {
      "include" : [
        "path: sourceFileRegexPatterns.include",
        "default value: []",
        "default value type: EmptyList",
        "what file names constitute a source file, relative to the 'inputDir' configuration item",
        "used to determine names",
        "list of regular expression patterns to include",
        "to be included, a file must match at least one include pattern, without matching any exclude patterns"
      ],
      "exclude" : [
        "path: sourceFileRegexPatterns.exclude",
        "default value: []",
        "default value type: EmptyList",
        "what file names constitute a source file, relative to the 'inputDir' configuration item",
        "used to determine names",
        "list of regular expression patterns to exclude",
        "list of regular expression patterns to include",
        "to be included, a file must match at least one include pattern, without matching any exclude patterns"
      ]
    },
    "nodeLimitForGraph" : [
      "path: nodeLimitForGraph",
      "default value: 50",
      "default value type: Integer",
      "the higher the number of files, the longer the graph takes to generate and the more useless it is",
      " if this limit is exceeded, the graph is not generated"
    ],
    "binaryFileRegexPatterns" : {
      "include" : [
        "path: binaryFileRegexPatterns.include",
        "default value: []",
        "default value type: EmptyList",
        "what file name constitutes a binary file, relative to the 'inputDir' configuration item",
        "used to determine dependency relationships between names",
        "list of regular expression patterns to include",
        "list of regular expression patterns to include",
        "to be included, a file must match at least one include pattern, without matching any exclude patterns"
      ],
      "exclude" : [
        "path: binaryFileRegexPatterns.exclude",
        "default value: []",
        "default value type: EmptyList",
        "what file name constitutes a binary file, relative to the 'inputDir' configuration item",
        "used to determine dependency relationships between names",
        "list of regular expression patterns to exclude",
        "list of regular expression patterns to include",
        "to be included, a file must match at least one include pattern, without matching any exclude patterns"
      ]
    }
  }
}
```

## Command Line

Fetch the artifact to your local maven repository
```
mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get \
    -DrepoUrl=https://repo1.maven.org/maven2 \
    -Dartifact=com.seanshubin.code.structure:code-structure-console:1.0.3
```


Run the code structure application
```
java -jar $HOME/.m2/repository/com/seanshubin/code/structure/code-structure-console/1.0.3/code-structure-console-1.0.3.jar
```

First parameter is the base name for your configuration file.
Default is "code-structure", which will result in a configuration file named "code-structure-config.json"

## Maven Plugin Configuration

Will run during the "verify" phase of the [maven lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)

```xml
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>com.seanshubin.code.structure</groupId>
                <artifactId>code-structure-maven</artifactId>
                <version>1.0.3</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>code-structure</goal>
                        </goals>
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

## Why these metrics fail the build

Although these metrics may seem arbitrary if you are not used to them, they exist for very good reasons. Your first
instinct may be to think "well that doesn't matter", but that is exactly the problem, you are not used to thinking about
these things because you didn't have the proper tooling to keep track of these things. Once you try a new project that
strictly applies these rules, even if you don't understand the rules at first, you will come to understand them once you
see how your code is affected by diligently following these rules.

See a more thorough description in the
[naming](/docs/naming.md) document

## Why check the quantity of errors rather than checking the errors individually
In a similar project I recorded the individual errors rather than the quantity of errors.

In practice, I found the following disadvantages.

- (minor) the error list would have to be updated for refactorings that moved code around without making anything worse
- (major) users got used to updating the error list even when the code was worse off, as there was no easy way for others to tell the difference between code getting better or worse

The solution here is to maintain an error count rather than an error list, with the following effects.

- (minor) no need to maintain a list of errors
- (minor) no need to update anything when just moving code around
- (major) anyone who makes the problem worse has to increase the maximumAllowedErrorCount, advertising their shame in version control for all eternity 

Recommended policy that a unit of work is not considered done until one of 3 situations are true

- error count goes up
  - An error metric has been introduced that was not there before, such as this application.  While the error count has gone up the errors have not, they were always there, just not detected
- error count goes down
  - An error has been fixed such that the error count has gone down by at least one 
- error count is zero
  - There are no unfixed errors, and there are no other available metrics to add

## Prerequisites

Tested with versions

[Java]( https://aws.amazon.com/corretto )
```
java -version
openjdk version "21.0.6" 2025-01-21 LTS
OpenJDK Runtime Environment Corretto-21.0.6.7.1 (build 21.0.6+7-LTS)
OpenJDK 64-Bit Server VM Corretto-21.0.6.7.1 (build 21.0.6+7-LTS, mixed mode, sharing)
```

[Maven]( https://maven.apache.org/ )

```
mvn -version
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: /opt/homebrew/Cellar/maven/3.9.9/libexec
Java version: 23.0.2, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk/23.0.2/libexec/openjdk.jdk/Contents/Home
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "15.3.2", arch: "aarch64", family: "mac"
```

[graphviz]( https://graphviz.org/ )
```
dot --version
dot - graphviz version 12.2.1 (20241206.2353)
```

## Scripts

```shell
./scripts/clean.sh
./scripts/test.sh
./scripts/build.sh
./scripts/run.sh
```
