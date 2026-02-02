package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.json.JsonMappers
import com.seanshubin.code.structure.model.ErrorType
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class QualityMetricsReport : Report {
    override val reportName: String = "quality-metrics"
    override val category: ReportCategory = ReportCategory.COUNT

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val metrics = mapOf(
            "inDirectCycle" to (validated.analysis.summary.errors[ErrorType.IN_DIRECT_CYCLE]?.count ?: 0),
            "inGroupCycle" to (validated.analysis.summary.errors[ErrorType.IN_GROUP_CYCLE]?.count ?: 0),
            "ancestorDependsOnDescendant" to (validated.analysis.summary.errors[ErrorType.ANCESTOR_DEPENDS_ON_DESCENDANT]?.count
                ?: 0),
            "descendantDependsOnAncestor" to (validated.analysis.summary.errors[ErrorType.DESCENDANT_DEPENDS_ON_ANCESTOR]?.count
                ?: 0)
        )

        val json = JsonMappers.pretty.writeValueAsString(metrics)
        val lines = json.lines()
        val path = reportDir.resolve("quality-metrics.json")

        return listOf(CreateFileCommand(reportName, path, lines))
    }
}