package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.jvmformat.ClassParser
import com.seanshubin.code.structure.relationparser.RelationDetail
import com.seanshubin.code.structure.relationparser.RelationParser
import java.nio.file.Path
import kotlin.io.path.extension

class DynamicRelationParser(
    classParser: ClassParser,
    beamParser: BeamParser
) : RelationParser {
    private val parserByExtension = mapOf(
        "class" to classParser,
        "jar" to classParser,
        "war" to classParser,
        "beam" to beamParser
    )

    override fun parseDependencies(path: Path, names: List<String>): List<RelationDetail> {
        val extension = path.extension
        val parser =
            parserByExtension[extension]
                ?: throw RuntimeException("No relation parser defined for extension '$extension'")
        return parser.parseDependencies(path, names)
    }
}
