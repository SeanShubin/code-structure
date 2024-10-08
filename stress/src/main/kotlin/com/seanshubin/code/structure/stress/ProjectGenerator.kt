package com.seanshubin.code.structure.stress

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

object ProjectGenerator {
    private val prefixParts = listOf("com", "seanshubin", "codestructure", "generated")
    private val namesFileName = "names.txt"
    private val relationsFileName = "relations.txt"

    fun generateProject(baseDir: Path) {
        val namesFile = baseDir.resolve(namesFileName)
        val names = loadNames(namesFile)
        val relationsFile = baseDir.resolve(relationsFileName)
        val relations = loadRelations(relationsFile)
        generateProject(baseDir, names, relations)
    }

    fun writeNames(dir: Path, names: List<String>) {
        writeLines(dir, namesFileName, names)
    }

    fun writeRelations(dir: Path, relations: List<Pair<String, String>>) {
        val lines = relations.map(::relationToLine)
        writeLines(dir, relationsFileName, lines)
    }

    private fun relationToLine(relation: Pair<String, String>): String {
        val (first, second) = relation
        return "$first -> $second"
    }

    private fun loadNames(namesFile: Path): List<String> {
        return Files.readAllLines(namesFile)
    }

    private fun loadRelations(relationsFile: Path): List<Pair<String, String>> {
        return Files.readAllLines(relationsFile).map { line ->
            val parts = line.split(" -> ")
            val first = parts[0]
            val second = parts[1]
            first to second
        }
    }

    private fun generateProject(baseDir: Path, names: List<String>, relations: List<Pair<String, String>>) {
        val compilationUnits = composeCompilationUnits(names, relations)
        writeCompilationUnits(baseDir, compilationUnits)
        writePomFile(baseDir)
        writeCodeStructureConfigFile(baseDir)
    }

    private fun composeCompilationUnits(
        names: List<String>,
        relations: List<Pair<String, String>>
    ): List<CompilationUnit> {
        val relationsByName: Map<String, List<Pair<String, String>>> = relations.groupBy { it.first }
        return names.map { name ->
            val dependencies = (relationsByName[name] ?: emptyList()).map { it.second }
            CompilationUnit(name, dependencies)
        }
    }

    private fun writeCompilationUnits(dir: Path, compilationUnits: List<CompilationUnit>) {
        compilationUnits.forEach {
            writeCompilationUnit(dir, it)
        }
    }

    private fun writeCompilationUnit(dir: Path, compilationUnit: CompilationUnit) {
        val fileName = compilationUnit.relativeFileName(prefixParts)
        val lines = compilationUnit.lines(prefixParts)
        writeLines(dir, fileName, lines)
    }

    private fun writeLines(dir: Path, fileName: String, lines: List<String>) {
        val path = dir.resolve(fileName)
        val parent = path.parent
        Files.createDirectories(parent)
        Files.write(path, lines, StandardCharsets.UTF_8)
    }

    private fun writePomFile(dir: Path) {
        val pomLines = mutableListOf<String>()
        pomLines.add("<project>")
        pomLines.add("    <modelVersion>4.0.0</modelVersion>")
        pomLines.add("    <groupId>${prefixParts.joinToString(".")}</groupId>")
        pomLines.add("    <artifactId>generated</artifactId>")
        pomLines.add("    <version>0.1.0</version>")
        pomLines.add("</project>")
        writeLines(dir, "pom.xml", pomLines)
    }

    private fun writeCodeStructureConfigFile(dir: Path) {
        val codeStructureConfigLines = """
            {
              "countAsErrors" : {
                "inDirectCycle" : true,
                "inGroupCycle" : true,
                "ancestorDependsOnDescendant" : true,
                "descendantDependsOnAncestor" : true
              },
              "maximumAllowedErrorCount" : 0,
              "inputDir" : ".",
              "outputDir" : "generated",
              "localDepth" : 1,
              "useObservationsCache" : false,
              "sourcePrefix" : "../",
              "sourceFileRegexPatterns" : {
                "include" : [ ".*\\.java"],
                "exclude" : [ ]
              },
              "nodeLimitForGraph" : 50,
              "binaryFileRegexPatterns" : {
                "include" : [ ".*\\.class" ],
                "exclude" : [ ]
              }
            }
        """.trimIndent().split("\n")
        writeLines(dir, "code-structure-config.json", codeStructureConfigLines)
    }
}