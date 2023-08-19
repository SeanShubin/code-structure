package com.seanshubin.code.structure.domain

import java.nio.file.Path

class ReportGeneratorImpl(
    private val reports: List<Report>,
    private val outputDir: Path,
) : ReportGenerator {
    private val reportDir = outputDir.resolve("reports")
    override fun generateReports(analysis: Analysis): List<CreateFileCommand> {
        val generateReportFunction = { report: Report -> generateReport(analysis, report) }
        return reports.map(generateReportFunction)
    }

    private fun generateReport(analysis: Analysis, report: Report): CreateFileCommand {
        val html = report.generate(analysis)
        val fileName = "${report.name}.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return CreateFileCommand(path, lines)
    }
}
