package com.seanshubin.code.structure.nameparser

interface SourceParserRepository {
    fun lookupByLanguage(language: String): SourceParser
}