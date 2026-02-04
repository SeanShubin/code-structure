package com.seanshubin.code.structure.filefinder

import java.nio.file.Path

interface FilterStats {
    fun recordMatch(category: String, type: String, pattern: String, file: Path)
    fun recordUnmatch(category: String, file: Path)
    fun registerPatterns(category: String, includePatterns: List<String>, excludePatterns: List<String>)
    fun getMatchedEvents(category: String): List<MatchedFilterEvent>
    fun getUnmatchedEvents(category: String): List<UnmatchedFilterEvent>
    fun getRegisteredIncludePatterns(category: String): List<String>
    fun getRegisteredExcludePatterns(category: String): List<String>
    fun getUnusedIncludePatterns(category: String): List<String>
    fun getUnusedExcludePatterns(category: String): List<String>
    fun getAllCategories(): List<String>
}
