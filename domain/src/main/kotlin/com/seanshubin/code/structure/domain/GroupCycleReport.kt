package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.BigListClassName
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class GroupCycleReport(private val nodeLimitForGraph: Int) : Report {
    override val reportName: String = "group-cycles"
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Page.tableOfContents)
        val groupCycleList = groupCycleList(validated.analysis.groupScopedAnalysisList)
        val htmlInsideBody = generateHtml(groupCycleList)
        val html = ReportHelper.wrapInTopLevelHtml(Page.inGroupCycle.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(Page.inGroupCycle.file)
        val lines = html.toLines()
        val topCommand = CreateFileCommand(reportName, path, lines)
        val graphCommands = commandsForAllCycleGraphs(reportDir, groupCycleList, parents)
        return listOf(topCommand) + graphCommands
    }

    private fun groupCycleList(groupScopedAnalysisList: List<Pair<List<String>, ScopedAnalysis>>): List<GroupCycle> {
        return groupScopedAnalysisList.flatMap { (group, scopedAnalysis) ->
            scopedAnalysis.cycleDetails.map {
                GroupCycle(group, it.names, it.references)
            }
        }
    }

    private fun commandsForAllCycleGraphs(
        reportDir: Path,
        groupCycleList: List<GroupCycle>,
        parents: List<Page>
    ): List<Command> {
        val parentsForCycle = parents + listOf(Page.inGroupCycle)
        return groupCycleList.flatMapIndexed { index, groupCycle ->
            commandsForCycleGraph(reportDir, index, groupCycle, parentsForCycle)
        }
    }

    private fun commandsForCycleGraph(
        reportDir: Path,
        index: Int,
        groupCycle: GroupCycle,
        parents: List<Page>
    ): List<Command> {
        val nodes = groupCycle.names.map { toDotNode(it) }
        return ReportHelper.graphCommands(
            reportName,
            reportDir,
            cycleName(index),
            nodes,
            nodeLimitForGraph,
            groupCycle.references,
            emptyList(),
            parents
        )
    }

    private fun cycleName(index: Int): String {
        val parts = listOf("group", "cycle") + listOf(index.toString())
        return parts.joinToString("-")
    }

    private fun toDotNode(name: String): DotNode =
        DotNode(
            id = name,
            text = name,
            link = null,
            color = "blue",
            bold = false
        )

    private fun generateHtml(groupCycles: List<GroupCycle>): List<HtmlElement> {
        return summaryElement(groupCycles) + cyclesElement(groupCycles)
    }

    private fun summaryElement(groupCycles: List<GroupCycle>): List<HtmlElement> {
        val header = HtmlElement.tagText("h2", "Summary")
        val countParagraph = HtmlElement.Tag("p", HtmlElement.Text("cycle count: ${groupCycles.size}"))
        val fragmentAnchors = composeFragmentAnchors(groupCycles)
        return listOf(header, countParagraph) + fragmentAnchors
    }

    private fun composeFragmentAnchors(groupCycles: List<GroupCycle>): List<HtmlElement> =
        groupCycles.indices.map(::composeFragmentAnchor)

    private fun composeFragmentAnchor(index: Int): HtmlElement {
        val title = cycleName(index)
        val link = "#$title"
        return anchor(title, link)
    }

    private fun cyclesElement(groupCycles: List<GroupCycle>): List<HtmlElement> {
        val header = HtmlElement.tagText("h2", "Cycles")
        return listOf(header) + groupCycles.flatMapIndexed(::cycleListElement)
    }

    private fun cycleListElement(listIndex: Int, groupCycle: GroupCycle): List<HtmlElement> {
        val id = cycleName(listIndex)
        val summaryAnchor = anchor(id, "$id.html")
        val summary = HtmlElement.Tag("h2", listOf(summaryAnchor), listOf("id" to id))
        val groupAnchor = composeGroupAnchor(groupCycle.group)
        val groupElement = HtmlElement.Tag("p", listOf(groupAnchor))
        val listElements = bigList(groupCycle.names, ::cycleElement, BigListClassName.COLUMN_1, "part")
        return listOf(summary) + listOf(groupElement) + listElements
    }

    private fun composeGroupAnchor(group: List<String>): HtmlElement {
        val title = "group: " + group.joinToString(", ", "[", "]")
        val link = CodeUnit(group).toUriName("group", ".html")
        return anchor(title, link)
    }

    private fun cycleElement(name: String): List<HtmlElement> {
        val text = HtmlElement.Text(name)
        val span = HtmlElement.Tag("span", listOf(text))
        return listOf(span)
    }

    private data class GroupCycle(
        val group: List<String>,
        val names: List<String>,
        val references: List<Pair<String, String>>
    ) {
        fun qualifiedNames(): List<String> =
            names.map { name -> CodeUnit(group + name).toName() }
    }
}
