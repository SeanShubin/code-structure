package com.seanshubin.code.structure.domain

import java.nio.file.Path

class GraphReport:Report {
    override fun generate(reportDir: Path, analysis: Analysis): List<Command> {
        return ReportHelper.graphCommands(reportDir, "graph", analysis.names, analysis.references)
    }
}
