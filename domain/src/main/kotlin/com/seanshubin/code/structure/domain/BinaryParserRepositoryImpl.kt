package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.binaryparser.BinaryParser
import com.seanshubin.code.structure.binaryparser.BinaryParserRepository
import com.seanshubin.code.structure.jvmformat.ClassParser

class BinaryParserRepositoryImpl(
    classParser: ClassParser,
    beamParser: BeamParser
) : BinaryParserRepository {
    private val parserByBytecodeFormat = mapOf(
        "class" to classParser,
        "beam" to beamParser
    )

    override fun lookupByBytecodeFormat(bytecodeFormat: String): BinaryParser {
        return parserByBytecodeFormat[bytecodeFormat]
            ?: throw RuntimeException("Unsupported bytecode format '$bytecodeFormat'")
    }
}