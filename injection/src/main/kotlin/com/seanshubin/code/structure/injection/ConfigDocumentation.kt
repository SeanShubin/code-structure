package com.seanshubin.code.structure.injection

object ConfigDocumentation {
    val inputDir = listOf("the directory from which to start scanning")
    val outputDir = listOf("the directory to place the report")
    val sourcePrefix = listOf("pre-pended to links in the report, so you can navigate directly to the source code from the report")
    val localDepth = listOf("How far to draw the dependency graph for a single ")
    val useObservationsCache = listOf(
        "if the observations file exists, use that instead of scanning the sources and binaries",
        "this is useful if you want to run 'what if' scenarios by manually changing the observations file",
    )
    val maximumAllowedErrorCount = listOf("if the number of errors exceeds this number, the build will fail")
    val includeJvmDynamicInvocations = listOf(
        "Clojure invokes methods dynamically, so the class dependency won't show up as a class in the constant pool (CONSTANT_Class - 7)",
        "However, the class name will still show up as a string, so we can get it as a string in the constant pool (CONSTANT_Utf8 - 1)",
        "Reading the string constants instead of class constants will also catch instances of Class.forName, but only if the completed string exists in the constant pool, it will not be able to detect it in cases where the string is constructed at runtime.",
    )
    val nodeLimitForGraph = listOf(
        "the higher the number of files, the longer the graph takes to generate and the more useless it is",
        " if this limit is exceeded, the graph is not generated"
        )
    private val regexSuffix = listOf(
        "list of regular expression patterns to include",
        "to be included, a file must match at least one include pattern, without matching any exclude patterns",
    )
    val sourceFileRegexPatternsInclude = listOf(
        "what file names constitute a source file, relative to the 'inputDir' configuration item",
        "used to determine names",
    ) + regexSuffix
    val sourceFileRegexPatternsExclude = listOf(
        "what file names constitute a source file, relative to the 'inputDir' configuration item",
        "used to determine names",
        "list of regular expression patterns to exclude",
    ) + regexSuffix
    val binaryFileRegexPatternsInclude = listOf(
        "what file name constitutes a binary file, relative to the 'inputDir' configuration item",
        "used to determine dependency relationships between names",
        "list of regular expression patterns to include",
    ) + regexSuffix
    val binaryFileRegexPatternsExclude = listOf(
        "what file name constitutes a binary file, relative to the 'inputDir' configuration item",
        "used to determine dependency relationships between names",
        "list of regular expression patterns to exclude",
    ) + regexSuffix
    val inDirectCycle = listOf(
        "Whether to include the number of code units in a direct cycle in the error count",
        "Direct cycles typically require changes in logic to fix, so they are riskier than the other metrics",
        "Set this to false if you want to focus on metrics that are easier to fix first"
    )
    val inGroupCycle = listOf(
        "Whether to include the number of group cycles in the error count",
        "Groups are packages in java, modules in elixir"
    )
    val ancestorDependsOnDescendant = listOf(
        "Whether to include the number cases where an ancestor depends on a descendant in error count",
        "Dependencies between super-categories and sub-categories indicate that files in the super-category weren't placed in a properly named sub-category",
        "Instead, organize your directory structure such that each directory either only contains other directories following the same rules, or only contains files"
    )
    val descendantDependsOnAncestor = listOf(
        "Whether to include the number cases where a descendant depends on a ancestor in error count",
        "Dependencies between super-categories and sub-categories indicate that files in the super-category weren't placed in a properly named sub-category",
        "Instead, organize your directory structure such that each directory either only contains other directories following the same rules, or only contains files"
    )
}
