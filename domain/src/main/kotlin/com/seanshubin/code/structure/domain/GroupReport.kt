package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.ReportHelper.composeGroupPages
import com.seanshubin.code.structure.domain.ReportHelper.groupPage
import com.seanshubin.code.structure.dot.DotNode
import java.nio.file.Path

class GroupReport : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        return validated.analysis.groupScopedAnalysisList.flatMap { (groupPath, groupAnalysis) ->
            singleGroupReport(reportDir, groupPath, validated.analysis, groupAnalysis)
        }
    }

    private fun singleGroupReport(
        reportDir: Path,
        groupPath: List<String>,
        analysis: Analysis,
        groupAnalysis: ScopedAnalysis
    ): List<Command> {
        val basePage = groupPage(groupPath)
        val baseName = basePage.id
        val parents = composeParents(groupPath)
        val nodes = groupAnalysis.names.map { name ->
            toDotNode(name, groupPath + name, analysis)
        }
        return ReportHelper.graphCommands(
            reportDir,
            baseName,
            nodes,
            groupAnalysis.references,
            groupAnalysis.cycles,
            parents
        )
    }

    private fun composeParents(groupPath: List<String>): List<Page> {
        val groupPages = composeGroupPages(groupPath)
        return listOf(Page.tableOfContents) + groupPages
    }

    private fun toDotNode(
        name: String,
        groupPath: List<String>,
        analysis: Analysis
    ): DotNode {
        val descendantCount = analysis.descendantCount(groupPath)
        val hasChildren = analysis.containsGroup(groupPath)
        val link =
            if (hasChildren) CodeUnit(groupPath).toUriName("group", ".html")
            else CodeUnit(groupPath).toUriName("local", ".html")
        val text =
            if (hasChildren) "$name ($descendantCount)"
            else "$name (local)"
        return DotNode(
            id = name,
            text = text,
            link = link,
            color = "blue",
            bold = false
        )
    }
}
