package com.seanshubin.code.structure.sourceparser

interface SourceParserRepository {
    fun lookupByLanguage(language: String): SourceParser
}