package com.seanshubin.code.structure.bytecodeformat

interface BinaryParserRepository {
    fun lookupByBytecodeFormat(bytecodeFormat: String): BinaryParser
}