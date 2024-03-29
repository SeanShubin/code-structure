package com.seanshubin.code.structure.relationparser

interface RelationParserRepository {
    fun lookupByBytecodeFormat(bytecodeFormat: BytecodeFormat): RelationParser
}
