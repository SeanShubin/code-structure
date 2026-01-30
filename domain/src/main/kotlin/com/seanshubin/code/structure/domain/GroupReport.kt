package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.ReportHelper.composeGroupPages
import com.seanshubin.code.structure.domain.ReportHelper.groupPage
import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil
import java.nio.file.Path

class GroupReport(
    private val nodeLimitForGraph: Int
) : Report {
    override val reportName: String = "groups"
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        return validated.analysis.groupScopedAnalysisList.flatMap { (groupPath, groupAnalysis) ->
            singleGroupReport(
                reportDir,
                validated.observations.sourcePrefix,
                groupPath,
                validated.analysis,
                groupAnalysis
            )
        }
    }

    private fun singleGroupReport(
        reportDir: Path,
        sourcePrefix: String,
        groupPath: List<String>,
        analysis: Analysis,
        groupAnalysis: ScopedAnalysis
    ): List<Command> {
        val basePage = groupPage(groupPath)
        val baseName = basePage.id
        val parents = composeParents(groupPath)
        val nodes = groupAnalysis.names.map { name ->
            toDotNode(name, sourcePrefix, groupPath + name, analysis)
        }
        val dependencyTable = dependencyTable(groupAnalysis)
        return ReportHelper.graphCommands(
            reportName,
            reportDir,
            baseName,
            nodes,
            nodeLimitForGraph,
            groupAnalysis.referenceReasons.keys.toList(),
            groupAnalysis.cycles,
            parents,
            dependencyTable
        )
    }

    private fun dependencyTable(
        groupAnalysis: ScopedAnalysis
    ): List<HtmlElement> {
        val caption = "dependency reasons"
        val captions = listOf("dependency", "reason")
        val list = groupAnalysis.referenceReasons.flatMap { (reference, reasons) ->
            val (first, second) = reference
            val dependencyString = "$first -> $second"
            val reasonStrings = reasons.map { (reasonFirst, reasonSecond) ->
                val reasonString = "$reasonFirst -> $reasonSecond"
                Pair(dependencyString, reasonString)
            }
            reasonStrings
        }
        val elementToRow: (Pair<String, String>) -> List<String> = { it.toList() }
        return HtmlElementUtil.createTable(list, captions, elementToRow, caption)
    }

    private fun composeParents(groupPath: List<String>): List<Page> {
        val groupPages = composeGroupPages(groupPath)
        return listOf(Page.tableOfContents) + groupPages
    }

    private fun toDotNode(
        leafName: String,
        sourcePrefix: String,
        groupPath: List<String>,
        analysis: Analysis
    ): DotNode {
        val descendantCount = analysis.descendantCount(groupPath)
        val hasChildren = analysis.containsGroup(groupPath)
        val codeUnit = CodeUnit(groupPath)
        val link =
            if (hasChildren) codeUnit.toUriName("group", ".html")
            else codeUnit.toSourceLink(sourcePrefix, analysis.sourceByName)
        val text =
            if (hasChildren) "$leafName ($descendantCount)"
            else leafName
        return DotNode(
            id = leafName,
            text = text,
            link = link,
            color = "blue",
            bold = false
        )
    }
}
