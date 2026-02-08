package com.seanshubin.code.structure.filefinder

import java.nio.file.Path
import java.util.regex.Pattern

class RegexFileMatcherWithStats(
    private val relativeToDir: Path,
    private val includePatterns: List<String>,
    private val excludePatterns: List<String>,
    private val stats: FilterStats,
    private val category: String
) : (Path) -> Boolean {
    private val includeRegexList = includePatterns.map(Pattern::compile)
    private val excludeRegexList = excludePatterns.map(Pattern::compile)

    init {
        stats.registerPatterns(category, includePatterns, excludePatterns)
    }

    override fun invoke(path: Path): Boolean {
        val relativePath = relativeToDir.relativize(path)
        val relativeString = relativePath.toString().removePrefix("./")

        val matchingIncludePattern = findMatchingPattern(relativeString, includeRegexList, includePatterns)
        val matchingExcludePattern = findMatchingPattern(relativeString, excludeRegexList, excludePatterns)

        if (matchingIncludePattern != null) {
            stats.recordMatch(category, "include", matchingIncludePattern, path)
        }
        if (matchingExcludePattern != null) {
            stats.recordMatch(category, "exclude", matchingExcludePattern, path)
        }

        val isIncluded = matchingIncludePattern != null
        val isExcluded = matchingExcludePattern != null
        val result = isIncluded && !isExcluded

        if (!isIncluded && !isExcluded) {
            stats.recordUnmatch(category, path)
        }

        return result
    }

    private fun findMatchingPattern(
        text: String,
        regexList: List<Pattern>,
        patternStrings: List<String>
    ): String? {
        regexList.forEachIndexed { index, pattern ->
            if (pattern.matcher(text).matches()) {
                return patternStrings[index]
            }
        }
        return null
    }
}
