# Code Structure

## Getting started

- Check the "Prerequisites" section
- Install, either by building from source or fetching it from a maven repo url
  - Build from source `./scripts/clean-install-skip-tests.sh`
  - Fetch from a repo at MAVEN_REPO_URL `./scripts/fetch-from-maven-repo-url.sh` 
- Run on your project with no parameters, this will create the default configuration file
  - java -jar $HOME/.m2/repository/com/seanshubin/code/structure/code-structure-console/1.0.0-SNAPSHOT/code-structure-console-1.0.0-SNAPSHOT.jar
  - If you need multiple configuration files, specify the base name of the configuration file as the first parameter
- Add regular expressions to the sourceFileRegexPatterns sections in the default configuration file so that all of your sources are included (see examples)
  - Run the application again
- Inspect the "Sources without corresponding Binaries" section of the generated report
  - Add regular expressions to the binaryFileRegexPatterns section so that all of your source files have corresponding binaries
  - Run the application again
- If your source language is Clojure, set includeJvmDynamicInvocations to true

## Configuration Explained

Example configuration:

```json
{
  "inputDir" : ".",
  "outputDir" : "generated/self",
  "nodeLimitForGraph" : 50,
  "sourcePrefix" : "https://github.com/SeanShubin/code-structure/blob/master/",
  "sourceFileRegexPatterns" : {
    "include" : [
      ".*\\.kt$"
    ],
    "exclude" : [ ]
  },
  "binaryFileRegexPatterns" : {
    "include" : [
      ".*/code-structure-console.jar$"
    ],
    "exclude" : [ ]
  },
  "countAsErrors" : {
    "inDirectCycle" : true,
    "inGroupCycle" : true,
    "ancestorDependsOnDescendant" : true,
    "descendantDependsOnAncestor" : true
  },
  "maximumAllowedErrorCount" : 0,
  "useObservationsCache": false,
  "includeJvmDynamicInvocations" : false
}
```

- command line arguments
    - one argument, the prefix for the config file, default is `code-structure`
    - this causes it to use `code-structure-config.json` as the main configuration file
- config file
    - inputDir
        - path, the directory from which to start scanning
    - outputDir
        - path, the directory to place the report
    - nodeLimitForGraph
        - the higher the number of files, the longer the graph takes to generate and the more useless it is
        - if this limit is exceeded, the graph is not generated
    - bytecodeFormat
        - the compiled language
        - used to determine the dependency structure
        - only considers what we have sources for
        - currently only "class" and "beam" are supported
    - sourcePrefix
        - pre-pended to links in the report, so you can navigate directly to the source code from the report
    - sourceFileRegexPatterns
        - what file names constitute a source file, relative to the `inputDir` configuration item
        - used to determine names
        - list of regex patterns to include, and list of regex patterns to exclude
        - to be included, a file must match at least one include pattern, without matching any exclude patterns
    - binaryFileRegexPatterns
        - what file name constitutes a binary file, relative to the `inputDir` configuration item
        - used to determine dependency relationships between names
    - countAsErrors
        - if true, will return a non-zero exit code if any new items show up in these categories
        - items in the file ending with `-existing-errors.json` do not count as new items
        - fail conditions are, from most important to least important, directCycle, groupCycle,
          ancestorDependsOnDescendant, descendantDependsOnAncestor
    - countAsErrors
        - directCycle
            - fail if there is a new cycle between the bottom level items, classes for kotlin and modules for elixir
        - groupCycle
            - fail if there is a new cycle at the group level, pacakges for kotlin and paths for elixir
        - ancestorDependsOnDescendant
            - fail if there is a new dependency going from the upper to lower levels of the hierarchy
        - descendantDependsOnAncestor
            - fail if there is a new dependency going from the lower to the upper level of the hierarchy
    - useObservationsCache
        - if the observations file exists, use that instead of scanning the sources and binaries
        - this is useful if you want to run "what if" scenarios by manually changing the observations file
    - maximumAllowedErrorCount
        - if the number of errors exceeds this number, the build will fail
    - includeJvmDynamicInvocations
        - Clojure invokes methods dynamically, so the class dependency won't show up as a class in the constant pool (CONSTANT_Class - 7)
        - However, the class name will still show up as a string, so we can get it as a string in the constant pool (CONSTANT_Utf8 - 1)
        - Reading the string constants instead of class constants will also catch instances of Class.forName, but only if the completed string exists in the constant pool, it will not be able to detect it in cases where the string is constructed at runtime.

## Command Line

`java -jar $HOME/.m2/repository/com/seanshubin/code/structure/code-structure-console/1.0.0-SNAPSHOT/code-structure-console-1.0.0-SNAPSHOT.jar`

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
                <version>1.0.0-SNAPSHOT</version>
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

[Java 17]( https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html )

```
java -version
openjdk version "17.0.1" 2021-10-19 LTS
OpenJDK Runtime Environment Corretto-17.0.1.12.1 (build 17.0.1+12-LTS)
OpenJDK 64-Bit Server VM Corretto-17.0.1.12.1 (build 17.0.1+12-LTS, mixed mode, sharing)
```

[Maven]( https://maven.apache.org/ )

```
mvn -version
Apache Maven 3.9.1 (2e178502fcdbffc201671fb2537d0cb4b4cc58f8)
Maven home: /usr/local/Cellar/maven/3.9.1/libexec
Java version: 17.0.1, vendor: Amazon.com Inc., runtime: /Library/Java/JavaVirtualMachines/amazon-corretto-17.jdk/Contents/Home
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "12.6.8", arch: "x86_64", family: "mac"
```

[graphviz]( https://graphviz.org/ )

```
dot --version
dot - graphviz version 12.0.0 (20240704.0754)
```

## Scripts

```shell
./scripts/clean.sh
./scripts/test.sh
./scripts/build.sh
./scripts/run.sh
```

## References
- https://kotlinlang.org/docs/dokka-maven.html