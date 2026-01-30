package com.seanshubin.code.structure.nameparser

object RegexUtil {
    fun findAllByRegex(regex: Regex, content: String): List<String> {
        val matches = regex.findAll(content).map { matchResult ->
            matchResult.groupValues[1]
        }.toList()
        return matches
    }

    fun findByRegex(regex: Regex, content: String): String {
        val matchResult = regex.find(content)
        if (matchResult == null) {
            throw RuntimeException("Content '$content' did not match pattern '$regex'")
        } else {
            return matchResult.groupValues[1]
        }
    }
}
