package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.jvmformat.ClassParser
import com.seanshubin.code.structure.relationparser.BytecodeFormat
import com.seanshubin.code.structure.relationparser.RelationParser
import com.seanshubin.code.structure.relationparser.RelationParserRepository

class RelationParserRepositoryImpl(
    classParser: ClassParser,
    beamParser: BeamParser
) : RelationParserRepository {
    private val parserByBytecodeFormat = mapOf(
        BytecodeFormat.CLASS to classParser,
        BytecodeFormat.BEAM to beamParser
    )

    override fun lookupByBytecodeFormat(bytecodeFormat: BytecodeFormat): RelationParser =
        parserByBytecodeFormat.getValue(bytecodeFormat)
}
