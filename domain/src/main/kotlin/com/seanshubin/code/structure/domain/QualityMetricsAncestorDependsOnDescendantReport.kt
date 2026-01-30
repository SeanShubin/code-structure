package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.json.JsonMappers
import java.nio.file.Path

class QualityMetricsAncestorDependsOnDescendantReport : Report {
    override val reportName: String = "quality-metrics-ancestorDependsOnDescendant"

    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val violations = validated.analysis.lineage.ancestorDependsOnDescendant
            .map { (ancestor, descendant) ->
                Violation(ancestor, descendant)
            }
            .sortedWith(compareBy({ it.ancestor }, { it.descendant }))

        val json = JsonMappers.pretty.writeValueAsString(violations)
        val lines = json.lines()
        val path = reportDir.resolve("quality-metrics-ancestorDependsOnDescendant.json")

        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private data class Violation(
        val ancestor: String,
        val descendant: String
    )
}
