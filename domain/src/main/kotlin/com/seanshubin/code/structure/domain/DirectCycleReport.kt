package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class DirectCycleReport : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Page.tableOfContents)
        val cycleInfo = validated.analysis.global.cycleInfo
        if(cycleInfo == null){
            val htmlInsideBody = generateNoCycleInformation()
            val html = ReportHelper.wrapInTopLevelHtml(Page.directCycles.caption, htmlInsideBody, parents)
            val path = reportDir.resolve(Page.directCycles.file)
            val lines = html.toLines()
            val topCommand = CreateFileCommand(path, lines)
            val cycleDetails = validated.analysis.global.cycleInfo?.cycleDetails
            val graphCommands = if(cycleDetails == null) {
                emptyList()
            } else {
                commandsForAllCycleGraphs(reportDir, cycleDetails, parents)
            }
            return listOf(topCommand) + graphCommands
        } else {
            val htmlInsideBody = generateHtml(cycleInfo.cycles)
            val html = ReportHelper.wrapInTopLevelHtml(Page.directCycles.caption, htmlInsideBody, parents)
            val path = reportDir.resolve(Page.directCycles.file)
            val lines = html.toLines()
            val topCommand = CreateFileCommand(path, lines)
            val cycleDetails = cycleInfo.cycleDetails
            val graphCommands = commandsForAllCycleGraphs(reportDir, cycleDetails, parents)
            return listOf(topCommand) + graphCommands
        }
    }
    private fun commandsForAllCycleGraphs(
        reportDir: Path,
        cycleDetails: List<CycleDetail>,
        parents: List<Page>
    ): List<Command> {
        val parentsForCycle = parents + listOf(Page.directCycles)
        return cycleDetails.flatMapIndexed { index, cycleDetail ->
            commandsForCycleGraph(reportDir, index, cycleDetail, parentsForCycle)
        }
    }

    private fun commandsForCycleGraph(
        reportDir: Path,
        index: Int,
        detail: CycleDetail,
        parents: List<Page>
    ): List<Command> {
        val nodes = detail.names.map { toDotNode(it) }
        return ReportHelper.graphCommands(
            reportDir,
            cycleName(index),
            nodes,
            detail.references,
            emptyList(),
            parents
        )
    }

    private fun cycleName(index: Int): String {
        val parts = listOf("direct", "cycle") + listOf(index.toString())
        return parts.joinToString("-")
    }

    private fun toDotNode(name: String): DotNode =
        DotNode(
            id = name,
            text = name,
            link = name.toCodeUnit().toUriName("local", ".html"),
            color = "blue",
            bold = false
        )

    private fun generateHtml(cycles: List<List<String>>): List<HtmlElement> {
        return summaryElement(cycles) + cyclesElement(cycles)
    }
    private fun generateNoCycleInformation(): List<HtmlElement> {
        val text = "If you need cycle information at this level, in your configuration, change reportType to ${ReportType.DETAILED.name.lowercase()}"
        val textElement = HtmlElement.Text(text)
        val paragraph = HtmlElement.Tag("p", textElement)
        return listOf(paragraph)
    }

    private fun summaryElement(cycles: List<List<String>>): List<HtmlElement> {
        val countParagraph = HtmlElement.Tag("p", HtmlElement.Text("cycle count: ${cycles.size}"))
        val fragmentAnchors = composeFragmentAnchors(cycles)
        return listOf(countParagraph) + fragmentAnchors
    }

    private fun composeFragmentAnchors(cycles: List<List<String>>): List<HtmlElement> =
        cycles.indices.map(::composeFragmentAnchor)

    private fun composeFragmentAnchor(index: Int): HtmlElement {
        val title = cycleName(index)
        val link = "#$title"
        return anchor(title, link)
    }

    private fun cyclesElement(cycles: List<List<String>>): List<HtmlElement> {
        return cycles.flatMapIndexed(::cycleListElement)
    }

    private fun cycleListElement(listIndex: Int, cycleList: List<String>): List<HtmlElement> {
        val id = cycleName(listIndex)
        val summaryAnchor = anchor(id, "$id.html")
        val summary = HtmlElement.Tag("h2", listOf(summaryAnchor), listOf("id" to id))
        val listElements = bigList(cycleList, ::cycleElement, "column-1", "part")
        return listOf(summary) + listElements
    }

    private fun cycleElement(name: String): List<HtmlElement> {
        val link = name.toCodeUnit().toUriName("local", ".html")
        return listOf(anchor(name, link))
    }
}
