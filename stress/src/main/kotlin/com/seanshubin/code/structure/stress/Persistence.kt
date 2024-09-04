package com.seanshubin.code.structure.stress

import com.seanshubin.code.structure.durationformat.DurationFormat
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Instant

class Persistence(
    baseDir: Path,
    private val homeDir: String,
    private val depth: Int,
    private val breadth: Int
) {
    private val prefixParts = listOf("com", "seanshubin", "codestructure", "generated")
    private val projectDir = baseDir.resolve("depth-$depth-breadth-$breadth")

    fun store(
        names: List<String>,
        relations: List<Pair<String, String>>,
        compilationUnits: List<CompilationUnit>
    ) {
        writeLines("names.txt", names)
        writeRelations(relations)
        writeCompilationUnits(compilationUnits)
        writePomFile()
        writeCodeStructureConfigFile()
    }

    private fun writeCodeStructureConfigFile() {
        val codeStructureConfigLines = """
            {
              "countAsErrors" : {
                "directCycle" : true,
                "groupCycle" : true,
                "ancestorDependsOnDescendant" : true,
                "descendantDependsOnAncestor" : true
              },
              "maximumAllowedErrorCount" : 0,
              "inputDir" : ".",
              "outputDir" : "generated",
              "localDepth" : 1,
              "useObservationsCache" : false,
              "sourcePrefix" : "",
              "sourceFileRegexPatterns" : {
                "include" : [ ".*\\.java"],
                "exclude" : [ ]
              },
              "nodeLimitMainGraph" : 100,
              "binaryFileRegexPatterns" : {
                "include" : [ ".*\\.class" ],
                "exclude" : [ ]
              }
            }
        """.trimIndent().split("\n")
        writeLines("code-structure-config.json", codeStructureConfigLines)
    }

    private fun writePomFile() {
        val pomLines = mutableListOf<String>()
        pomLines.add("<project>")
        pomLines.add("    <modelVersion>4.0.0</modelVersion>")
        pomLines.add("    <groupId>${prefixParts.joinToString(".")}</groupId>")
        pomLines.add("    <artifactId>generated</artifactId>")
        pomLines.add("    <version>0.1.0</version>")
        pomLines.add("</project>")
        writeLines("pom.xml", pomLines)
    }

    fun createReadme(start:Instant, end: Instant) {
        val lines = mutableListOf<String>()
        lines.add("# Generated Project")
        lines.add("Java project for performance tuning the code structure application")
        lines.add("")
        lines.add("- depth = $depth")
        lines.add("- breadth = $breadth")
        val duration = Duration.between(start, end)
        val millis = duration.toMillis()
        val durationString = DurationFormat.milliseconds.format(millis)
        lines.add("- time = $durationString")
        writeLines("README.md", lines)
    }

    private fun writeCompilationUnits(compilationUnits: List<CompilationUnit>) {
        compilationUnits.forEach(::writeCompilationUnit)
    }

    private fun writeCompilationUnit(compilationUnit: CompilationUnit) {
        val fileName = compilationUnit.relativeFileName(prefixParts)
        val lines = compilationUnit.lines(prefixParts)
        writeLines(fileName, lines)
    }

    private fun writeRelations(relations: List<Pair<String, String>>) {
        val lines = relations.map(::relationToLine)
        writeLines("relations.txt", lines)
    }

    private fun relationToLine(relation: Pair<String, String>): String {
        val (first, second) = relation
        return "$first -> $second"
    }

    private fun writeLines(fileName: String, lines: List<String>) {
        val path = projectDir.resolve(fileName)
        val parent = path.parent
        Files.createDirectories(parent)
        Files.write(path, lines, StandardCharsets.UTF_8)
    }
}