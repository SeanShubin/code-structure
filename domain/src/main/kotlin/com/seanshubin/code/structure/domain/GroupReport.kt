package com.seanshubin.code.structure.domain

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
        val groupPages =
            if (groupPath.isEmpty()) emptyList()
            else groupPages(groupPath.take(groupPath.size - 1))
        return listOf(Page.tableOfContents) + groupPages
    }

    private fun groupPage(groupPath: List<String>): Page {
        val codeUnit = CodeUnit(groupPath)
        val caption = codeUnit.caption("Group")
        val id = codeUnit.id("group")
        return Page.createIdCaption(id, caption)
    }

    private fun groupPages(groupPath: List<String>): List<Page> =
        if (groupPath.isEmpty()) listOf(groupPage(groupPath))
        else groupPages(groupPath.take(groupPath.size - 1)) + groupPage(groupPath)

    private fun toDotNode(
        name: String,
        groupPath: List<String>,
        analysis: Analysis
    ): DotNode {
        val descendantCount = analysis.descendantCount(groupPath)
        val hasChildren = analysis.containsGroup(groupPath)
        val link = if (hasChildren) CodeUnit(groupPath).toUriName("group", ".html") else null
        val text = if (descendantCount == 0) name else "$name ($descendantCount)"
        return DotNode(
            id = name,
            text = text,
            link = link,
            color = "blue",
            bold = false
        )
    }
}
