package com.seanshubin.code.structure.sourceparser

object RegexUtil {
    fun findRegex(regex: Regex, content: String): List<String> {
        val matches = regex.findAll(content).map { matchResult ->
            matchResult.groupValues[1]
        }.toList()
        return matches
    }
}