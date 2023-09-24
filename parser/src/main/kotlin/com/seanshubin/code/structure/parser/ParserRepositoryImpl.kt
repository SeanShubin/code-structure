package com.seanshubin.code.structure.parser

class ParserRepositoryImpl(
    kotlinParser: KotlinParser,
    elixirParser: ElixirParser
) : ParserRepository {
    private val parserByLanguage = mapOf(
        "kotlin" to kotlinParser,
        "elixir" to elixirParser
    )

    override fun lookupByLanguage(language: String): Parser {
        return parserByLanguage[language] ?: throw RuntimeException("Unsupported source language '$language'")
    }
}
