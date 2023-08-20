package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
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
            <html>
              <head>
                <title>
                  sources
                </title>
              </head>
              <body>
                <p>
                  base/dir/file-1.kt
                </p>
                <p>
                  base/dir/file-2.kt
                </p>
                <p>
                  base/dir/file-3.kt
                </p>
              </body>
            </html>
        """.trimIndent()
        val expectedPath = Paths.get("generated/reports/sources.html")

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
        val reportWrapper = ReportWrapperStub()
        val reportGenerator = ReportGeneratorImpl(reports, outputDir, reportWrapper)
        val sourceFilePaths = sourceFiles.map { Paths.get(it) }
        val observations = Observations(sourceFilePaths)
        val analysis = Analysis(observations)
    }

    class ReportStub(override val name: String) : Report {
        override fun generate(analysis: Analysis): List<HtmlElement> {
            val sources = analysis.observations.sourceFiles.map {
                val text = HtmlElement.Text(it.toString())
                HtmlElement.Tag("p", text)
            }
            return sources
        }
    }

    class ReportWrapperStub : ReportWrapper {
        override fun wrapInTopLevelHtml(name: String, htmlInsideBody: List<HtmlElement>): HtmlElement {
            val titleText = HtmlElement.Text(name)
            val title = HtmlElement.Tag("title", titleText)
            val head = HtmlElement.Tag("head", title)
            val body = HtmlElement.Tag("body", htmlInsideBody)
            val html = HtmlElement.Tag("html", head, body)
            return html
        }
    }
}
