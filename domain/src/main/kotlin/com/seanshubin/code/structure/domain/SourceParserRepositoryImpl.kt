package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.elixirsyntax.ElixirParser
import com.seanshubin.code.structure.kotlinsyntax.KotlinParser
import com.seanshubin.code.structure.nameparser.NameParser
import com.seanshubin.code.structure.nameparser.SourceParserRepository

class SourceParserRepositoryImpl(
    kotlinSourceParser: KotlinParser,
    elixirParser: ElixirParser
) : SourceParserRepository {
    private val parserByLanguage = mapOf(
        "kotlin" to kotlinSourceParser,
        "elixir" to elixirParser
    )

    override fun lookupByLanguage(language: String): NameParser {
        return parserByLanguage[language] ?: throw RuntimeException("Unsupported source language '$language'")
    }
}
