package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.json.JsonMappers
import java.nio.file.Path

class QualityMetricsInDirectCycleReport : Report {
    override val reportName: String = "quality-metrics-inDirectCycle"
    override val category: ReportCategory = ReportCategory.DIFF

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val cycles = validated.analysis.global.cycles
        val json = JsonMappers.pretty.writeValueAsString(cycles)
        val lines = json.lines()
        val path = reportDir.resolve("quality-metrics-inDirectCycle.json")

        return listOf(CreateFileCommand(reportName, path, lines))
    }
}
