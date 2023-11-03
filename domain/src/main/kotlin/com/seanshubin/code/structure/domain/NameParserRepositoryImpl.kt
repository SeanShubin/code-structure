package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.elixirsyntax.ElixirParser
import com.seanshubin.code.structure.kotlinsyntax.KotlinParser
import com.seanshubin.code.structure.nameparser.NameParser
import com.seanshubin.code.structure.nameparser.NameParserRepository
import com.seanshubin.code.structure.scalasyntax.ScalaParser

class NameParserRepositoryImpl(
    kotlinParser: KotlinParser,
    elixirParser: ElixirParser,
    scalaParser: ScalaParser
) : NameParserRepository {
    private val parserByLanguage = mapOf(
        "kotlin" to kotlinParser,
        "elixir" to elixirParser,
        "scala" to scalaParser
    )

    override fun lookupByLanguage(language: String): NameParser {
        return parserByLanguage[language] ?: throw RuntimeException("Unsupported source language '$language'")
    }
}
