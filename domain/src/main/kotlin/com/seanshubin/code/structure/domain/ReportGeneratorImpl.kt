package com.seanshubin.code.structure.domain

import java.nio.file.Path

class ReportGeneratorImpl(
    private val reports: List<Report>,
    private val finalReports: List<Report>,
    private val outputDir: Path,
    private val timer: Timer,
) : ReportGenerator {
    override fun generateReports(validated: Validated): List<Command> {
        val generateReportFunction = { report: Report ->
            timer.monitor("report.${report.name}") {
                report.generate(outputDir, validated)
            }
        }
        return reports.flatMap(generateReportFunction)
    }

    override fun generateFinalReports(validated: Validated): List<Command> {
        val generateReportFunction = { report: Report -> report.generate(outputDir, validated) }
        return finalReports.flatMap(generateReportFunction)
    }
}
