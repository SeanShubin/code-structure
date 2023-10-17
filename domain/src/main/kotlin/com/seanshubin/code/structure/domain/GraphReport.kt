package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.dot.DotNode
import java.nio.file.Path

class GraphReport : Report {
    override fun generate(reportDir: Path, analysis: Analysis): List<Command> {
        val parents = listOf(Pages.tableOfContents)
        val nodes = analysis.names.map { toDotNode(it, LinkCreator.local) }
        return ReportHelper.graphCommands(
            reportDir,
            Pages.graph.id,
            nodes,
            analysis.references,
            parents
        )
    }

    private fun toDotNode(name: String, createLink: (String) -> String): DotNode =
        DotNode(
            id = name,
            text = name,
            link = createLink(name),
            color = "blue",
            bold = false
        )
}
