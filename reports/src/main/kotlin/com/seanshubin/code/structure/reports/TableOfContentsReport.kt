package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.BigListClassName
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import com.seanshubin.code.structure.model.Analysis
import com.seanshubin.code.structure.model.CodeUnit
import com.seanshubin.code.structure.model.ErrorType
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class TableOfContentsReport(
    private val nodeLimitForGraph: Int
) : Report {
    override val reportName: String = "table-of-contents"
    override val category: ReportCategory = ReportCategory.BROWSE

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val parents = emptyList<Page>()
        val children = listOf(
            Page.groups,
            annotateWithNumber(Page.entryPoints, validated.analysis.global.entryPoints.size),
            annotateWithNumber(
                Page.inDirectCycle,
                validated.analysis.summary.errors.getValue(ErrorType.IN_DIRECT_CYCLE).count
            ),
            annotateWithNumber(
                Page.inGroupCycle,
                validated.analysis.summary.errors.getValue(ErrorType.IN_GROUP_CYCLE).count
            ),
            annotateWithNumber(
                Page.lineageAncestorDescendant,
                validated.analysis.summary.errors.getValue(ErrorType.ANCESTOR_DEPENDS_ON_DESCENDANT).count
            ),
            annotateWithNumber(
                Page.lineageDescendantAncestor,
                validated.analysis.summary.errors.getValue(ErrorType.DESCENDANT_DEPENDS_ON_ANCESTOR).count
            ),
            annotateWithNumber(Page.codeUnits, validated.analysis.global.names.size),
            annotateWithNumber(Page.sources, validated.observations.sources.size),
            annotateWithNumber(Page.binaries, validated.observations.binaries.size),
            annotateWithNumber(Page.dependencies, validated.analysis.global.referenceReasons.size),
            Page.graph,
            annotateWithNumber(Page.missingBinaries, validated.observations.missingBinaries.size),
            Page.filterStatistics,
            Page.timing
        )
        val listElements = bigList(children, ::generateAnchor, BigListClassName.COLUMN_1, caption = null)

        val topLevelGroup = validated.analysis.groupScopedAnalysisList.find { it.first.isEmpty() }
        return if (topLevelGroup != null) {
            val (groupPath, groupAnalysis) = topLevelGroup
            val nodes = groupAnalysis.names.map { name ->
                toDotNode(name, validated.observations.sourcePrefix, listOf(name), validated.analysis)
            }

            ReportHelper.graphCommands(
                reportName = reportName,
                reportDir = reportDir,
                baseName = "index",
                nodes = nodes,
                nodeLimitForGraph = nodeLimitForGraph,
                references = groupAnalysis.referenceReasons.keys.toList(),
                cycles = groupAnalysis.cycles,
                parents = parents,
                belowGraph = listElements,
                title = "Code Structure"
            )
        } else {
            val title = "Code Structure"
            val html = ReportHelper.wrapInTopLevelHtml(title, listElements, parents)
            val path = reportDir.resolve("index.html")
            listOf(CreateFileCommand(reportName, path, html.toLines()))
        }
    }

    private fun generateAnchor(page: Page): List<HtmlElement> =
        listOf(anchor(page.caption, page.link))

    private fun annotateWithNumber(page: Page, value: Int): Page = object : Page {
        override val caption: String get() = if (value == 0) page.caption else "${page.caption} ($value)"
        override val link: String get() = page.link
        override val file: String get() = page.file
        override val id: String get() = page.id
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
