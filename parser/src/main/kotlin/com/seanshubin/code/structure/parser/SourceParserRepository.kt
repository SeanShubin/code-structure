package com.seanshubin.code.structure.parser

interface SourceParserRepository {
    fun lookupByLanguage(language: String): SourceParser
}