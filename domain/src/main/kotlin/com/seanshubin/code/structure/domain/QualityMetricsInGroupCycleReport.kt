package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.json.JsonMappers
import java.nio.file.Path

class QualityMetricsInGroupCycleReport : Report {
    override val reportName: String = "quality-metrics-inGroupCycle"

    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val groupCycles = validated.analysis.groupCycles.map { (group, cycleDetail) ->
            GroupCycleDetail(group, cycleDetail.names)
        }
        val json = JsonMappers.pretty.writeValueAsString(groupCycles)
        val lines = json.lines()
        val path = reportDir.resolve("quality-metrics-inGroupCycle.json")

        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private data class GroupCycleDetail(
        val group: List<String>,
        val cycle: List<String>
    )
}
