package com.seanshubin.code.structure.relationparser

interface BinaryParserRepository {
    fun lookupByBytecodeFormat(bytecodeFormat: String): BinaryParser
}