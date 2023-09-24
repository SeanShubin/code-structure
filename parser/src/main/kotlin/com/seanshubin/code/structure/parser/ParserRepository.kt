package com.seanshubin.code.structure.parser

interface ParserRepository {
    fun lookupByLanguage(language:String):Parser
}