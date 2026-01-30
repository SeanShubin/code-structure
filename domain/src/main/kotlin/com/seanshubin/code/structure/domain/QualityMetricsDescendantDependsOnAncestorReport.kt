package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.json.JsonMappers
import java.nio.file.Path

class QualityMetricsDescendantDependsOnAncestorReport : Report {
    override val reportName: String = "quality-metrics-descendantDependsOnAncestor"

    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val violations = validated.analysis.lineage.descendantDependsOnAncestor
            .map { (descendant, ancestor) ->
                Violation(descendant, ancestor)
            }
            .sortedWith(compareBy({ it.descendant }, { it.ancestor }))

        val json = JsonMappers.pretty.writeValueAsString(violations)
        val lines = json.lines()
        val path = reportDir.resolve("quality-metrics-descendantDependsOnAncestor.json")

        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private data class Violation(
        val descendant: String,
        val ancestor: String
    )
}