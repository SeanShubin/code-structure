package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.jvmformat.ClassParser
import com.seanshubin.code.structure.relationparser.RelationDetail
import com.seanshubin.code.structure.relationparser.RelationParser
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptRelationParser
import java.nio.file.Path
import kotlin.io.path.extension

class DynamicRelationParser(
    classParser: ClassParser,
    beamParser: BeamParser,
    typeScriptRelationParser: TypeScriptRelationParser
) : RelationParser {
    private val parserByExtension = mapOf(
        "class" to classParser,
        "jar" to classParser,
        "war" to classParser,
        "beam" to beamParser,
        "ts" to typeScriptRelationParser,
        "tsx" to typeScriptRelationParser
    )

    override fun parseDependencies(path: Path, names: List<String>): List<RelationDetail> {
        val extension = path.extension
        val parser =
            parserByExtension[extension]
                ?: throw RuntimeException("No relation parser defined for extension '$extension' on file '$path'")
        return parser.parseDependencies(path, names)
    }
}
