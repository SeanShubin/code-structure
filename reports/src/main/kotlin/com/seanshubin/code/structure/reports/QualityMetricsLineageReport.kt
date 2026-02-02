package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.json.JsonMappers
import com.seanshubin.code.structure.model.Lineage
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class QualityMetricsLineageReport(
    private val fileName: String,
    private val direction: (Lineage) -> List<Pair<String, String>>,
    private val toViolation: (String, String) -> Any
) : Report {
    override val reportName: String = "quality-metrics-$fileName"
    override val category: ReportCategory = ReportCategory.DIFF

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val lineage: List<Pair<String, String>> = direction(validated.analysis.lineage)
        val violations = lineage
            .map { (first, second) -> toViolation(first, second) }
            .sortedBy { JsonMappers.compact.writeValueAsString(it) }

        val json = JsonMappers.pretty.writeValueAsString(violations)
        val lines = json.lines()
        val path = reportDir.resolve("quality-metrics-$fileName.json")

        return listOf(CreateFileCommand(reportName, path, lines))
    }
}
