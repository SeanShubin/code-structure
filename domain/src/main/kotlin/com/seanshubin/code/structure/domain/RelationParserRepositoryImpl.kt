package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.jvmformat.ClassParser
import com.seanshubin.code.structure.relationparser.RelationParser
import com.seanshubin.code.structure.relationparser.RelationParserRepository

class RelationParserRepositoryImpl(
    classParser: ClassParser,
    beamParser: BeamParser
) : RelationParserRepository {
    private val parserByBytecodeFormat = mapOf(
        "class" to classParser,
        "beam" to beamParser
    )

    override fun lookupByBytecodeFormat(bytecodeFormat: String): RelationParser {
        return parserByBytecodeFormat[bytecodeFormat]
            ?: throw RuntimeException("Unsupported bytecode format '$bytecodeFormat'")
    }
}
