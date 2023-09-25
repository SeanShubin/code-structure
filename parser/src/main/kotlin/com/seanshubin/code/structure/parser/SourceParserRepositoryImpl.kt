package com.seanshubin.code.structure.parser

class SourceParserRepositoryImpl(
    kotlinParser: KotlinParser,
    elixirParser: ElixirParser
) : SourceParserRepository {
    private val parserByLanguage = mapOf(
        "kotlin" to kotlinParser,
        "elixir" to elixirParser
    )

    override fun lookupByLanguage(language: String): SourceParser {
        return parserByLanguage[language] ?: throw RuntimeException("Unsupported source language '$language'")
    }
}
