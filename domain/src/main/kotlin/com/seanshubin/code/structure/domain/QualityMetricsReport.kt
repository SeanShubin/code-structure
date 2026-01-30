package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.json.JsonMappers
import java.nio.file.Path

class QualityMetricsReport : Report {
    override val reportName: String = "quality-metrics"

    override fun generate(reportDir: Path, validated: Validated): List<Command> {
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