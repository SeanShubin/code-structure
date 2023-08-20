package com.seanshubin.code.structure.domain

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportGeneratorTest {
    @Test
    fun generateReports() {
        // given
        val outputDirName = "generated"
        val sourceFiles = listOf(
            "base/dir/file-1.kt",
            "base/dir/file-2.kt",
            "base/dir/file-3.kt"
        )
        val reportName = "sources"
        val tester = Tester(outputDirName, reportName, sourceFiles)
        val expectedLines = """
            base/dir/file-1.kt
            base/dir/file-2.kt
            base/dir/file-3.kt
        """.trimIndent()
        val expectedPath = Paths.get("generated/reports/sources.txt")

        // when
        val actual = tester.reportGenerator.generateReports(tester.analysis)

        // then
        assertEquals(1, actual.size)
        val command = actual[0]
        assertEquals(expectedPath, command.path)
        assertEquals(expectedLines, command.lines.joinToString("\n"))
    }

    class Tester(
        outputDirName: String,
        reportName: String,
        sourceFiles: List<String>
    ) {
        val outputDir = Paths.get(outputDirName)
        val files = FakeFiles()
        val report = ReportStub(reportName)
        val reports = listOf(report)
        val reportGenerator = ReportGeneratorImpl(reports, outputDir)
        val sourceFilePaths = sourceFiles.map { Paths.get(it) }
        val observations = Observations(sourceFilePaths)
        val analysis = Analysis(observations)
    }

    class ReportStub(override val name: String) : Report {
        override fun generate(reportDir: Path, analysis: Analysis): CreateFileCommand {
            val path = reportDir.resolve("$name.txt")
            val lines = analysis.observations.sourceFiles.map {
                it.toString()
            }
            return CreateFileCommand(path, lines)
        }
    }
}
