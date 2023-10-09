package com.seanshubin.code.structure.parser

import com.seanshubin.code.structure.parser.RegexUtil.findRegex
import java.nio.file.Path

class KotlinSourceParserImpl(private val relativeToDir:Path) : KotlinSourceParser {
    private val packageRegex = Regex("""^(?:[^\n ].*)?package +([.\w]*)""", RegexOption.MULTILINE)
    private val interfaceRegex = Regex("""^(?:[^\n ].*)?interface +([.\w]*)""", RegexOption.MULTILINE)
    private val classRegex = Regex("""^(?:[^\n ].*)?class +([.\w]*)""", RegexOption.MULTILINE)
    private val objectRegex = Regex("""^(?:[^\n ].*)?object +([.\w]*)""", RegexOption.MULTILINE)
    override fun parseSource(path: Path, content: String): SourceDetail {
        val relativePath = relativeToDir.relativize(path)
        val language = "elixir"
        val packages = findRegex(packageRegex, content)
        val errorLines = mutableListOf<String>()
        if (packages.size > 1) {
            errorLines.add("too many packages in $relativePath, found ${packages.size}")
            packages.forEachIndexed { index, line ->
                errorLines.add("  [$index] '$line'")
            }
            return SourceDetail(
                relativePath,
                language,
                emptyList(),
                errorLines
            )
        }
        val classes = findRegex(classRegex, content)
        val interfaces = findRegex(interfaceRegex, content)
        val objects = findRegex(objectRegex, content)
        val prefix = if (packages.isEmpty()) "" else packages[0] + "."
        val names = classes + interfaces + objects
        val qualifiedNames = names.map {
            "$prefix$it"
        }
        return SourceDetail(
            relativePath,
            language,
            qualifiedNames,
            errorLines
        )
    }
}
