package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlUtil.anchor
import com.seanshubin.code.structure.reports.ReportHtmlUtil.bigList
import com.seanshubin.code.structure.model.CodeUnit
import com.seanshubin.code.structure.model.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.model.CycleDetail
import com.seanshubin.code.structure.model.ScopedAnalysis
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class DirectCycleReport(private val nodeLimitForGraph: Int) : Report {
    override val reportName: String = "direct-cycles"
    override val category: ReportCategory = ReportCategory.BROWSE
    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val parents = listOf(Page.tableOfContents)
        val htmlInsideBody = generateHtml(validated)
        val html = ReportHelper.wrapInTopLevelHtml(Page.inDirectCycle.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(Page.inDirectCycle.file)
        val lines = html.toLines()
        val topCommand = CreateFileCommand(reportName, path, lines)
        val sourcePrefix = validated.observations.sourcePrefix
        val sourceByName = validated.analysis.sourceByName
        val graphCommands = commandsForAllCycleGraphs(
            reportDir,
            validated.analysis.global,
            parents,
            sourcePrefix,
            sourceByName
        )
        return listOf(topCommand) + graphCommands
    }

    private fun commandsForAllCycleGraphs(
        reportDir: Path,
        analysis: ScopedAnalysis,
        parents: List<Page>,
        sourcePrefix: String,
        sourceByName: Map<String, List<Path>>
    ): List<Command> {
        val parentsForCycle = parents + listOf(Page.inDirectCycle)
        return analysis.cycleDetails.flatMapIndexed { index, cycleDetail ->
            commandsForCycleGraph(reportDir, index, cycleDetail, parentsForCycle, sourcePrefix, sourceByName)
        }
    }

    private fun commandsForCycleGraph(
        reportDir: Path,
        index: Int,
        detail: CycleDetail,
        parents: List<Page>,
        sourcePrefix: String,
        sourceByName: Map<String, List<Path>>
    ): List<Command> {
        val nodes = detail.names.map { toDotNode(it.toCodeUnit(), sourcePrefix, sourceByName) }
        return ReportHelper.graphCommands(
            reportName,
            reportDir,
            cycleName(index),
            nodes,
            nodeLimitForGraph,
            detail.references,
            emptyList(),
            parents
        )
    }

    private fun cycleName(index: Int): String {
        val parts = listOf("direct", "cycle") + listOf(index.toString())
        return parts.joinToString("-")
    }

    private fun toDotNode(codeUnit: CodeUnit, sourcePrefix: String, sourceByName: Map<String, List<Path>>): DotNode {
        val link = codeUnit.toSourceLink(sourcePrefix, sourceByName)
        return DotNode(
            id = codeUnit.toName(),
            text = codeUnit.toName(),
            link = link,
            color = "blue",
            bold = false
        )
    }

    private fun generateHtml(validated: Validated): List<HtmlElement> {
        val cycles = validated.analysis.global.cycles
        return summaryElement(cycles) + cyclesElement(cycles)
    }

    private fun summaryElement(cycles: List<List<String>>): List<HtmlElement> {
        val header = HtmlElement.tagText("h2", "Summary")
        val countParagraph = HtmlElement.Tag("p", HtmlElement.Text("cycle count: ${cycles.size}"))
        val fragmentAnchors = composeFragmentAnchors(cycles)
        return listOf(header, countParagraph) + fragmentAnchors
    }

    private fun composeFragmentAnchors(cycles: List<List<String>>): List<HtmlElement> =
        cycles.indices.map(::composeFragmentAnchor)

    private fun composeFragmentAnchor(index: Int): HtmlElement {
        val title = cycleName(index)
        val link = "#$title"
        return anchor(title, link)
    }

    private fun cyclesElement(cycles: List<List<String>>): List<HtmlElement> {
        val header = HtmlElement.tagText("h2", "Cycles")
        return listOf(header) + cycles.flatMapIndexed(::cycleListElement)
    }

    private fun cycleListElement(listIndex: Int, cycleList: List<String>): List<HtmlElement> {
        val id = cycleName(listIndex)
        val summaryAnchor = anchor(id, "$id.html")
        val summary = HtmlElement.Tag("h2", listOf(summaryAnchor), listOf("id" to id))
        val listElements = bigList(cycleList, ::cycleElement, BigListClassName.COLUMN_1, "part")
        return listOf(summary) + listElements
    }

    private fun cycleElement(name: String): List<HtmlElement> {
        return listOf(HtmlElement.tagText("span", name))
    }
}
