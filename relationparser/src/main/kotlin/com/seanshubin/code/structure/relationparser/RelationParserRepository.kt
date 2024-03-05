package com.seanshubin.code.structure.relationparser

interface RelationParserRepository {
    fun lookupByBytecodeFormat(bytecodeFormat: String): RelationParser
    fun supportedBytecodeFormatNames(): List<String>
}