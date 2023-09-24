package com.seanshubin.code.structure.parser

import java.nio.file.Path

class KotlinParser : Parser {
    private val packageRegex = Regex("""^(?:[^\n ].*)?package +([.\w]*)""", RegexOption.MULTILINE)
    private val interfaceRegex = Regex("""^(?:[^\n ].*)?interface +([.\w]*)""", RegexOption.MULTILINE)
    private val classRegex = Regex("""^(?:[^\n ].*)?class +([.\w]*)""", RegexOption.MULTILINE)
    private val objectRegex = Regex("""^(?:[^\n ].*)?object +([.\w]*)""", RegexOption.MULTILINE)
    override fun parseSource(path: Path, content: String): SourceDetail {
        if (path.toString() == "./domain/src/test/kotlin/com/seanshubin/code/structure/domain/FakeFiles.kt") {
            println(path)
        }
        val language = "kotlin"
        val packages = findRegex(packageRegex, content)
        val errorLines = mutableListOf<String>()
        if (packages.size > 1) {
            errorLines.add("too many packages in $path, found ${packages.size}")
            packages.forEachIndexed { index, line ->
                errorLines.add("  [$index] '$line'")
            }
            return SourceDetail(
                path,
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
            path,
            language,
            qualifiedNames,
            errorLines
        )
    }

    private fun findRegex(regex: Regex, content: String): List<String> {
        val matches = regex.findAll(content).map { matchResult ->
            matchResult.groupValues[1]
        }.toList()
        return matches
    }
}
