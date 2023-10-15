package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class CycleReport : Report {
    override fun generate(reportDir: Path, analysis: Analysis): List<Command> {
        val parents = listOf(Pages.tableOfContents)
        val name = Pages.cycles.name
        val htmlInsideBody = generateHtml(analysis)
        val html = ReportHelper.wrapInTopLevelHtml(name, htmlInsideBody, parents)
        val fileName = Pages.cycles.fileName
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        val topCommand = CreateFileCommand(path, lines)
        val graphCommands = commandsForAllCycleGraphs(reportDir, analysis, parents)
        return listOf(topCommand) + graphCommands
    }

    private fun commandsForAllCycleGraphs(reportDir: Path, analysis: Analysis, parents: List<Page>): List<Command> {
        val parentsForCycle = parents + listOf(Pages.cycles)
        return analysis.cycleDetails.flatMapIndexed { index, cycleDetail ->
            commandsForCycleGraph(reportDir, index, cycleDetail, parentsForCycle)
        }
    }

    private fun commandsForCycleGraph(
        reportDir: Path,
        index: Int,
        detail: CycleDetail,
        parents: List<Page>
    ): List<Command> {
        return ReportHelper.graphCommands(
            reportDir,
            "cycle-$index",
            detail.names,
            detail.references,
            LinkCreator.local,
            parents
        )
    }

    private fun generateHtml(analysis: Analysis): List<HtmlElement> {
        return summaryElement(analysis) + cyclesElement(analysis)
    }

    private fun summaryElement(analysis: Analysis): List<HtmlElement> {
        return listOf(
            HtmlElement.Tag("p", HtmlElement.Text("cycle count: ${analysis.cycles.size}"))
        )
    }

    private fun cyclesElement(analysis: Analysis): List<HtmlElement> {
        return analysis.cycles.flatMapIndexed(::cycleListElement)
    }

    private fun cycleListElement(listIndex: Int, cycleList: List<String>): List<HtmlElement> {
        val summaryAnchor = anchor("cycle-$listIndex", "cycle-$listIndex.html")
        val summary = HtmlElement.Tag("h2", listOf(summaryAnchor))
        val partCountText = HtmlElement.Text("part count: ${cycleList.size}")
        val partCountParagraph = HtmlElement.Tag("p", listOf(partCountText))
        val cycleElements = cycleList.map(::cycleElement)
        val listElement = bigList(cycleElements)
        return listOf(summary, partCountParagraph) + listElement
    }

    private fun cycleElement(name: String): HtmlElement {
        val link = LinkCreator.local(name)
        return anchor(name, link)
    }
}
