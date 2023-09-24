package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.parser.SourceDetail
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportGeneratorTest {
    @Test
    fun generateReports() {
        // given
        val inputDirName = "input"
        val outputDirName = "generated"
        val sourceFiles = listOf(
            "base/dir/file-1.kt",
            "base/dir/file-2.kt",
            "base/dir/file-3.kt"
        )
        val tester = Tester(inputDirName, outputDirName, sourceFiles)
        val expectedLines = """
            base/dir/file-1.kt
            base/dir/file-2.kt
            base/dir/file-3.kt
        """.trimIndent()
        val expectedPath = Paths.get("generated/reports/report.txt")

        // when
        val actual = tester.reportGenerator.generateReports(tester.analysis)

        // then
        assertEquals(1, actual.size)
        val command = actual[0]
        assertEquals(expectedPath, command.path)
        assertEquals(expectedLines, command.lines.joinToString("\n"))
    }

    class Tester(
        inputDirName: String,
        outputDirName: String,
        sourceFiles: List<String>
    ) {
        val inputDir = Paths.get(inputDirName)
        val outputDir = Paths.get(outputDirName)
        val sourcePrefix = ""
        val files = FakeFiles()
        val report = ReportStub()
        val reports = listOf(report)
        val reportGenerator = ReportGeneratorImpl(reports, outputDir)
        val sourceFilePaths = sourceFiles.map { Paths.get(it) }
        val sourceDetailsByPath = emptyMap<Path, SourceDetail>()
        val observations = Observations(
            inputDir,
            sourcePrefix,
            sourceFilePaths,
            sourceDetailsByPath
        )
        val analysis = Analysis(observations)
    }

    class ReportStub() : Report {
        override fun generate(reportDir: Path, analysis: Analysis): List<CreateFileCommand> {
            val path = reportDir.resolve("report.txt")
            val lines = analysis.observations.sourceFiles.map {
                it.toString()
            }
            return listOf(CreateFileCommand(path, lines))
        }
    }
}
