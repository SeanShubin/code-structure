package com.seanshubin.code.structure.kotlinsyntax

import com.seanshubin.code.structure.nameparser.NameDetail
import com.seanshubin.code.structure.nameparser.RegexUtil
import com.seanshubin.code.structure.nameparser.RegexUtil.findAllByRegex
import java.nio.file.Path

class KotlinParserImpl(private val relativeToDir: Path) : KotlinParser {
    private val fileNameRegex = Regex("""(\w+)\.kt$""")
    private val packageRegex = Regex("""^(?:[^\n ].*)?package +([.\w]*)""", RegexOption.MULTILINE)
    private val interfaceRegex = Regex("""^(?:[^\n ].*)?interface +([.\w]*)""", RegexOption.MULTILINE)
    private val classRegex = Regex("""^(?:[^\n ].*)?class +([.\w]*)""", RegexOption.MULTILINE)
    private val objectRegex = Regex("""^(?:[^\n ].*)?object +([.\w]*)""", RegexOption.MULTILINE)
    override fun parseName(path: Path, content: String): NameDetail {
        val relativePath = relativeToDir.relativize(path)
        val language = "kotlin"
        val packages = findAllByRegex(packageRegex, content)
        val errorLines = mutableListOf<String>()
        if (packages.size > 1) {
            errorLines.add("too many packages in $relativePath, found ${packages.size}")
            packages.forEachIndexed { index, line ->
                errorLines.add("  [$index] '$line'")
            }
            return NameDetail(
                relativePath,
                language,
                emptyList(),
                errorLines
            )
        }
        val fileNameMatch = RegexUtil.findByRegex(fileNameRegex, relativePath.toString())
        val classes = findAllByRegex(classRegex, content)
        val interfaces = findAllByRegex(interfaceRegex, content)
        val objects = findAllByRegex(objectRegex, content)
        val prefix = if (packages.isEmpty()) "" else packages[0] + "."
        val names = listOf(fileNameMatch) + classes + interfaces + objects
        val qualifiedNames = names.map {
            "$prefix$it"
        }.distinct()
        return NameDetail(
            relativePath,
            language,
            qualifiedNames,
            errorLines
        )
    }
}
