package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.json.JsonMappers
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class QualityMetricsInGroupCycleReport : Report {
    override val reportName: String = "quality-metrics-inGroupCycle"
    override val category: ReportCategory = ReportCategory.DIFF

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
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
