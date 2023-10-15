package com.seanshubin.code.structure.domain

import java.nio.file.Path

class GraphReport:Report {
    override fun generate(reportDir: Path, analysis: Analysis): List<Command> {
        val parents = listOf(Pages.tableOfContents)
        val name = Pages.graph.name
        return ReportHelper.graphCommands(
            reportDir,
            name,
            analysis.names,
            analysis.references,
            LinkCreator.local,
            parents)
    }
}
