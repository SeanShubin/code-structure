package com.seanshubin.code.structure.parser

class ParserRepositoryImpl(kotlinParser: Parser):ParserRepository {
    private val parserByLanguage = mapOf("kotlin" to kotlinParser)
    override fun lookupByLanguage(language: String): Parser {
        return parserByLanguage[language] ?: throw RuntimeException("Unsupported source language '$language'")
    }
}
