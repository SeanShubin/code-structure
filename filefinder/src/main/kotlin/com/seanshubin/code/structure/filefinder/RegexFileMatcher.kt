package com.seanshubin.code.structure.filefinder

import java.nio.file.Path
import java.util.regex.Pattern

class RegexFileMatcher(
    private val relativeToDir: Path,
    includeRegexPatterns: List<String>,
    excludeRegexPatterns: List<String>
) : (Path) -> Boolean {
    private val includeRegexList = includeRegexPatterns.map(Pattern::compile)
    private val excludeRegexList = excludeRegexPatterns.map(Pattern::compile)
    override fun invoke(path: Path): Boolean {
        val relativePath = relativeToDir.relativize(path).toString().replace('\\', '/')
        return isIncluded(relativePath) && !isExcluded(relativePath)
    }

    private fun isIncluded(file: String): Boolean {
        val result = includeRegexList.any {
            it.matcher(file).matches()
        }
        return result
    }

    private fun isExcluded(file: String): Boolean {
        val result = excludeRegexList.any {
            it.matcher(file).matches()
        }
        return result
    }
}
