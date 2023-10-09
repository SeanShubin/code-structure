package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.elixirsyntax.ElixirParser
import com.seanshubin.code.structure.kotlinsyntax.KotlinSourceParser
import com.seanshubin.code.structure.sourceparser.SourceParser
import com.seanshubin.code.structure.sourceparser.SourceParserRepository

class SourceParserRepositoryImpl(
    kotlinSourceParser: KotlinSourceParser,
    elixirParser: ElixirParser
) : SourceParserRepository {
    private val parserByLanguage = mapOf(
        "kotlin" to kotlinSourceParser,
        "elixir" to elixirParser
    )

    override fun lookupByLanguage(language: String): SourceParser {
        return parserByLanguage[language] ?: throw RuntimeException("Unsupported source language '$language'")
    }
}
