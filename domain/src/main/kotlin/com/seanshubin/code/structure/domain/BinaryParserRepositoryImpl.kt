package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.relationparser.RelationParser
import com.seanshubin.code.structure.relationparser.BinaryParserRepository
import com.seanshubin.code.structure.jvmformat.ClassParser

class BinaryParserRepositoryImpl(
    classParser: ClassParser,
    beamParser: BeamParser
) : BinaryParserRepository {
    private val parserByBytecodeFormat = mapOf(
        "class" to classParser,
        "beam" to beamParser
    )

    override fun lookupByBytecodeFormat(bytecodeFormat: String): RelationParser {
        return parserByBytecodeFormat[bytecodeFormat]
            ?: throw RuntimeException("Unsupported bytecode format '$bytecodeFormat'")
    }
}
