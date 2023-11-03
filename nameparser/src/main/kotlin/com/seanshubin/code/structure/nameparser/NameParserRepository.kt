package com.seanshubin.code.structure.nameparser

interface NameParserRepository {
    fun lookupByLanguage(language: String): NameParser
}