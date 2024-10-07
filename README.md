# Code Structure

## Getting started

- Check the "Prerequisites" section
- Run on self with `./scripts/clean-build-run-skip-tests.sh`
- Run on your project with no parameters, this will create the default configuration file
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
    "directCycle" : true,
    "groupCycle" : true,
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

## Maven Plugin Configuration

```xml
<plugin>
    <groupId>com.seanshubin.code.structure</groupId>
    <artifactId>code-structure-parent</artifactId>
    <version>0.1.0</version>
    <configuration>
        <configBaseName>code-structure</configBaseName>
    </configuration>
</plugin>
```

## Why these metrics fail the build

Although these metrics may seem arbitrary if you are not used to them, they exist for very good reasons. Your first
instinct may be to think "well that doesn't matter", but that is exactly the problem, you are not used to thinking about
these things because you didn't have the proper tooling to keep track of these things. Once you try a new project that
strictly applies these rules, even if you don't understand the rules at first, you will come to understand them once you
see how your code is affected by diligently following these rules.

See a more thorough description in the
[naming](/docs/naming.md) document

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

## Work in Progress

- optionally turn off direct cycle checking
- failure based on quantity
- maven plugin
- spaghettification number
- summary report
- error report
- make local report optional
- ConfigHelp
- EnumUtil
- RelationParserRepository
- BytecodeFormat