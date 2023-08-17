package com.seanshubin.code.structure.filefinder

import java.nio.file.Path
import java.util.regex.Pattern

class RegexFileMatcher(
    includeRegexPatterns: List<String>,
    excludeRegexPatterns: List<String>
) : (Path) -> Boolean {
    private val includeRegexList = includeRegexPatterns.map(Pattern::compile)
    private val excludeRegexList = excludeRegexPatterns.map(Pattern::compile)
    override fun invoke(path: Path): Boolean {
        return isIncluded(path) && !isExcluded(path)
    }

    private fun isIncluded(file: Path): Boolean {
        val result = includeRegexList.any {
            it.matcher(file.toString()).matches()
        }
        return result
    }

    private fun isExcluded(file: Path): Boolean {
        val result = excludeRegexList.any {
            it.matcher(file.toString()).matches()
        }
        return result
    }
}
