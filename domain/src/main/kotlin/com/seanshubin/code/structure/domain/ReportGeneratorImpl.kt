package com.seanshubin.code.structure.domain

import java.nio.file.Path

class ReportGeneratorImpl(
    private val reports: List<Report>,
    private val finalReports: List<Report>,
    outputDir: Path
) : ReportGenerator {
    private val reportDir = outputDir.resolve("reports")
    override fun generateReports(analysis: Analysis): List<Command> {
        val generateReportFunction = { report: Report -> report.generate(reportDir, analysis) }
        return reports.flatMap(generateReportFunction)
    }

    override fun generateFinalReports(analysis: Analysis): List<Command> {
        val generateReportFunction = { report: Report -> report.generate(reportDir, analysis) }
        return finalReports.flatMap(generateReportFunction)
    }
}
