package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class CycleReport : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Pages.tableOfContents)
        val name = Pages.cycles.name
        val htmlInsideBody = generateHtml(validated.analysis.global)
        val html = ReportHelper.wrapInTopLevelHtml(name, htmlInsideBody, parents)
        val path = Pages.cycles.reportFilePath(reportDir)
        val lines = html.toLines()
        val topCommand = CreateFileCommand(path, lines)
        val graphCommands = commandsForAllCycleGraphs(reportDir, validated.analysis.global, parents)
        return listOf(topCommand) + graphCommands
    }

    private fun commandsForAllCycleGraphs(
        reportDir: Path,
        analysis: ScopedAnalysis,
        parents: List<Page>
    ): List<Command> {
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
        val nodes = detail.names.map { toDotNode(it, LinkCreator.local) }
        return ReportHelper.graphCommands(
            reportDir,
            "cycle-$index",
            nodes,
            detail.references,
            emptyList(),
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

    private fun generateHtml(analysis: ScopedAnalysis): List<HtmlElement> {
        return summaryElement(analysis) + cyclesElement(analysis)
    }

    private fun summaryElement(analysis: ScopedAnalysis): List<HtmlElement> {
        val countParagraph = HtmlElement.Tag("p", HtmlElement.Text("cycle count: ${analysis.cycles.size}"))
        val fragmentAnchors = composeFragmentAnchors(analysis)
        return listOf(countParagraph) + fragmentAnchors
    }

    private fun composeFragmentAnchors(analysis: ScopedAnalysis):List<HtmlElement> =
        analysis.cycles.mapIndexed(::composeFragmentAnchor)

    private fun composeFragmentAnchor(index:Int, cycle:List<String>):HtmlElement{
        val title = "cycle-$index"
        val link = "#$title"
        return anchor(title, link)
    }

    private fun cyclesElement(analysis: ScopedAnalysis): List<HtmlElement> {
        return analysis.cycles.flatMapIndexed(::cycleListElement)
    }

    private fun cycleListElement(listIndex: Int, cycleList: List<String>): List<HtmlElement> {
        val id = "cycle-$listIndex"
        val summaryAnchor = anchor(id, "$id.html")
        val summary = HtmlElement.Tag("h2", listOf(summaryAnchor), listOf("id" to id))
        val partCountText = HtmlElement.Text("part count: ${cycleList.size}")
        val partCountParagraph = HtmlElement.Tag("p", listOf(partCountText))
        val listElements = bigList(cycleList, ::cycleElement, "big-list", "cycle")
        return listOf(summary, partCountParagraph) + listElements
    }

    private fun cycleElement(name: String): List<HtmlElement> {
        val link = LinkCreator.local(name)
        return listOf(anchor(name, link))
    }
}
