package com.seanshubin.code.structure.filefinder

import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class FilterStatsImpl : FilterStats {
    private val matchedEvents = ConcurrentLinkedQueue<MatchedFilterEvent>()
    private val unmatchedEvents = ConcurrentLinkedQueue<UnmatchedFilterEvent>()
    private val registeredIncludePatterns = ConcurrentHashMap<String, List<String>>()
    private val registeredExcludePatterns = ConcurrentHashMap<String, List<String>>()

    override fun recordMatch(category: String, type: String, pattern: String, file: Path) {
        matchedEvents.add(MatchedFilterEvent(category, type, pattern, file))
    }

    override fun recordUnmatch(category: String, file: Path) {
        unmatchedEvents.add(UnmatchedFilterEvent(category, file))
    }

    override fun registerPatterns(category: String, includePatterns: List<String>, excludePatterns: List<String>) {
        registeredIncludePatterns[category] = includePatterns
        registeredExcludePatterns[category] = excludePatterns
    }

    override fun getMatchedEvents(category: String): List<MatchedFilterEvent> =
        matchedEvents.filter { it.category == category }

    override fun getUnmatchedEvents(category: String): List<UnmatchedFilterEvent> =
        unmatchedEvents.filter { it.category == category }

    override fun getRegisteredIncludePatterns(category: String): List<String> =
        registeredIncludePatterns[category] ?: emptyList()

    override fun getRegisteredExcludePatterns(category: String): List<String> =
        registeredExcludePatterns[category] ?: emptyList()

    override fun getUnusedIncludePatterns(category: String): List<String> {
        val registered = getRegisteredIncludePatterns(category).toSet()
        val used = getMatchedEvents(category).filter { it.type == "include" }.map { it.pattern }.toSet()
        return (registered - used).sorted()
    }

    override fun getUnusedExcludePatterns(category: String): List<String> {
        val registered = getRegisteredExcludePatterns(category).toSet()
        val used = getMatchedEvents(category).filter { it.type == "exclude" }.map { it.pattern }.toSet()
        return (registered - used).sorted()
    }

    override fun getAllCategories(): List<String> =
        (registeredIncludePatterns.keys + registeredExcludePatterns.keys).distinct().sorted()
}
