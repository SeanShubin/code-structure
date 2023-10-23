# Code Structure

## Getting started
- Check the "Prerequisites" section
- Run on self with `./scripts/clean-test-build-run.sh`

## Configuration Explained

Example configuration:
```json
{
  "inputDir" : ".",
  "outputDir" : "generated/self",
  "language" : "kotlin",
  "localDepth" : 2,
  "nodeLimitMainGraph" : 100,
  "bytecodeFormat" : "class",
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
  "failConditions" : {
    "directCycle" : true,
    "groupCycle" : true,
    "ancestorDependsOnDescendant" : true,
    "descendantDependsOnAncestor" : true
  }
}
```

- command line arguments
  - one argument, the prefix for the config file, default is `code-structure`
  - this cause it to use `code-structure-config.json` as the main configuration file
  - and if there are any errors, it will create the default ignore list at `code-structure-existing-errors.json`
  - to reset the errors to ignore to the current state, delete the `code-structure-existing-errors.json` file
- config file
  - inputDir
    - path, the directory from which to start scanning
  - outputDir
    - path, the directory to place the report
  - language
    - the source language, currently only kotlin and elixir supported
    - used to determine the names of things defined in each file
  - localDepth
    - when looking at the graph of a single file, how many levels of associated files to look at
    - for example, if depth is 1, only look at the files the subject immediately depends on, or is immediately depended on by
    - if depth is 2, follow the same rule from each file included in depth 1
    - and so on for depths higher than 2
  - nodeLimitMainGraph
    - the primary "graph" report includes ALL files
    - the higher the number of files, the longer the graph takes to generate and the more useless it is
    - if this limit is exceeded, the global graph report is not generated 
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
  - failConditions
    - if true, will return a non-zero exit code if any new items show up in these categories
    - items in the file ending with `-existing-errors.json` do not count as new items
    - fail conditions are, from most important to least important, directCycle, groupCycle, ancestorDependsOnDescendant, descendantDependsOnAncestor
  - failConditions
    - directCycle
      - fail if there is a new cycle between the bottom level items, classes for kotlin and modules for elixir
    - groupCycle
      - fail if there is a new cycle at the group level, pacakges for kotlin and paths for elixir
    - ancestorDependsOnDescendant
      - fail if there is a new dependency going from the upper to lower levels of the hierarchy
    - descendantDependsOnAncestor
      - fail if there is a new dependency going from the lower to the upper level of the hierarchy 

## Why these metrics fail the build
Although these metrics may seem arbitrary if you are not used to them, they exist for very good reasons.
Your first instinct may be to think "well that doesn't matter", but that is exactly the problem, you are not used to thinking about these things because you didn't have the proper tooling to keep track of these things.
Once you try a new project that strictly applies these rules, even if you don't understand the rules at first, you will come to understand them once you see how your code is affected by diligently following these rules.

### Rule #1 no direct cycles
This rule exists because of the stable dependencies principle.
The stable dependencies principle is that modules may only depend on modules more stable than they are.
Stable does not mean they don't change, it means that the cost of breaking backwards compatibility is higher because of the things that depend on it.
Every application needs code at all levels of stability.
The stable parts are the unchanging rules you depend on, that don't change often once set.
The non-stable parts allow you to rapidly adapt to customer needs.
Consider this dependency structure
- a -> b
- b -> c
- c -> a

If a depends on b, and b depends on c, and c depends on a, you have a dependency cycle.
By logical deduction, if you have a cycle, you must have at least one violation of the stable dependencies principle.
By detecting cycles, this program narrows down where a violation of the stable dependencies principle must be,
but only a human with understanding of the intent behind the dependency structure can determine which dependency is incorrect.

## Rule #2 no group cycles
Consider this dependency structure
- module-a.unit-d -> module-b.unit-e
- module-b.unit-f -> module-c.unit-g
- module-c.unit-h -> module-a.unit-i

There are no direct dependencies, but the overall code was grouped in a way so the modules have a dependency cycle, even though no units have a cycle.
So there is no "direct" violation of the stable dependencies principle, so this may seem like no big deal.
However, it has detected a problem in the way you have been organizing your code.
The ONLY reason this seems normal, is that you never had the ability to detect the problem, so ended up not paying as much attention as you should have to the high level organizational structure of your application.
These cycles are easier to fix than direct cycles because they typically do not require logic changes.
It is also a good palace to start if your direct cycles are complicated.
By organizing at the high level first, you can prioritize which direct cycles to address.

## Rule #3 no ancestor may depend on a descendant
This is a bit less intuitive at first, because it involves a violation of the stable dependencies principle without it being made obvious with a dependency cycle.
Consider the following structure.
- module-a.unit-f -> module-a.module-b.unit-g
- module-a.unit-f -> module-a.module-c.unit-h
- module-a.unit-f -> module-a.module-d.unit-i

What we have here is a higher level module depending on lower level modules.
Even though there are no cycles, we have the general category depending on more specific categories.
Instead, the more generic code in module a should be moved into a sibling module of module-b, module-c, and module-d.
This gives you a chance to give module a more specific name and understand the dependency structure more clearly.
- module-a.module-e.unit-f -> module-a.module-b.unit-f
- module-a.module-e.unit-f -> module-a.module-c.unit-g
- module-a.module-e.unit-f -> module-a.module-d.unit-h

Now the relationship is more clear at the high level, as we can now express it as
- module-e -> module-b
- module-e -> module-c
- module-e -> module-d

## Rule #4 no descendant may depend on an ancestor
The least intuitive of the rules, but don't disregard it until you have tried it.
Once you see how applying this rule has changed the code, ask yourself if the code is better or worse for it.
Consider the following structure.
- module-a.module-b.unit-h -> module-a.unit-f
- module-a.module-c.unit-i -> module-a.unit-f
- module-a.module-d.unit-j -> module-a.unit-g
- module-a.module-e.unit-k -> module-a.unit-g

We have not violated the stable dependencies principle, as long as unit-f is the most stable.
However, remember that only human understanding of intent can really detect violations of the stable dependencies principle,
and humans express there intention through names.
Therefore, the better our names, the better able we are to organize the code sensibly.
If instead of depending on an ancestor module, we made it a sibling module, it forces us to think of a more specific name for the code located in the ancestor modules.
If we find it hard to come up with a name that matches everything in the parent, then we have detected something important, that there is more than once concept in the parent.
This means we actually end up with more than one sibling.
Consider what happens when you push these from the parent to a sibling.
- module-a.module-b.unit-h -> module-a.module-f.unit-f
- module-a.module-c.unit-i -> module-a.module-f.unit-f
- module-a.module-d.unit-j -> module-a.module-g.unit-g
- module-a.module-e.unit-k -> module-a.module-g.unit-g

Now at the high level, we have more explicitly named and easier to understand structure
- module-b -> module-f
- module-c -> module-f
- module-d -> module-g
- module-e -> module-g

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
dot - graphviz version 8.0.2 (20230410.1723)
```

## Scripts

```shell
./scripts/clean.sh
./scripts/test.sh
./scripts/build.sh
./scripts/run.sh
```
