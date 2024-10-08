package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.dot.DotNode
import java.nio.file.Path

class GraphReport(private val nodeLimitForGraph: Int) : Report {
    override val reportName: String = "graph"
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Page.tableOfContents)
        val analysis = validated.analysis
        val nodes = analysis.global.names.map(::toDotNode)
        return ReportHelper.graphCommands(
            reportName,
            reportDir,
            Page.graph.id,
            nodes,
            nodeLimitForGraph,
            analysis.global.referenceReasons.keys.toList(),
            analysis.global.cycles,
            parents
        )
    }

    private fun toDotNode(name: String): DotNode =
        DotNode(
            id = name,
            text = name,
            link = null,
            color = "blue",
            bold = false
        )
}
