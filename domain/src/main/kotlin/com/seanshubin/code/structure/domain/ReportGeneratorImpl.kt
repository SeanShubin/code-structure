package com.seanshubin.code.structure.domain

import java.nio.file.Path

class ReportGeneratorImpl(
    private val reports: List<Report>,
    private val finalReports: List<Report>,
    outputDir: Path
) : ReportGenerator {
    private val reportDir = outputDir.resolve("reports")
    override fun generateReports(validated: Validated): List<Command> {
        val generateReportFunction = { report: Report -> report.generate(reportDir, validated) }
        return reports.flatMap(generateReportFunction)
    }

    override fun generateFinalReports(validated: Validated): List<Command> {
        val generateReportFunction = { report: Report -> report.generate(reportDir, validated) }
        return finalReports.flatMap(generateReportFunction)
    }
}
