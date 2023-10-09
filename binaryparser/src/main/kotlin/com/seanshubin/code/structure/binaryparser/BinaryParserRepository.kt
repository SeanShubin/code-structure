package com.seanshubin.code.structure.binaryparser

interface BinaryParserRepository {
    fun lookupByBytecodeFormat(bytecodeFormat: String): BinaryParser
}