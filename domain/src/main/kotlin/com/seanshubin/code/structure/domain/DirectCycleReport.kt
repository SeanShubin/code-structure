package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.BigListClassName
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class DirectCycleReport(private val nodeLimitForGraph: Int) : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Page.tableOfContents)
        val htmlInsideBody = generateHtml(validated)
        val html = ReportHelper.wrapInTopLevelHtml(Page.directCycles.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(Page.directCycles.file)
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
        val parentsForCycle = parents + listOf(Page.directCycles)
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
        val nodes = detail.names.map { toDotNode(it) }
        return ReportHelper.graphCommands(
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

    private fun toDotNode(name: String): DotNode =
        DotNode(
            id = name,
            text = name,
            link = name.toCodeUnit().toUriName("local", ".html"),
            color = "blue",
            bold = false
        )

    private fun differencesElement(
        cycles: List<List<String>>,
        inDirectCycle: List<String>,
        cycleElementFunction: (String) -> List<HtmlElement>
    ): List<HtmlElement> {
        val configured = inDirectCycle.toSet()
        val existing = cycles.flatten().toSet()
        val newCycles = (existing - configured).toList().sorted()
        val fixedCycles = (configured - existing).toList().sorted()
        return differentCyclesElement(
            "Newly in cycle",
            newCycles,
            cycleElementFunction
        ) + differentCyclesElement("No longer in cycle", fixedCycles, cycleElementFunction)
    }

    private fun differentCyclesElement(
        caption: String,
        cycles: List<String>,
        cycleElementFunction: (String) -> List<HtmlElement>
    ): List<HtmlElement> {
        if (cycles.isEmpty()) return emptyList()
        val header = HtmlElement.tagText("h2", caption)
        val list = bigList(
            cycles,
            cycleElementFunction,
            BigListClassName.COLUMN_1,
            "in cycle"
        )
        return listOf(header) + list
    }

    private fun generateHtml(validated: Validated): List<HtmlElement> {
        val cycleElementFunction = createCycleElementFunction(validated.analysis.global.names)
        val configuredErrors = validated.observations.configuredErrors
        val cycles = validated.analysis.global.cycles
        val differences = if (configuredErrors == null) {
            emptyList()
        } else {
            differencesElement(cycles, configuredErrors.inDirectCycle, cycleElementFunction)
        }
        return differences + summaryElement(cycles) + cyclesElement(cycles)
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
        val link = name.toCodeUnit().toUriName("local", ".html")
        return listOf(anchor(name, link))
    }

    private fun cycleElementThatDoesNotExist(name: String): List<HtmlElement> {
        return listOf(HtmlElement.tagText("span", "$name (no longer exists)"))
    }

    private fun createCycleElementFunction(existingNames: List<String>): (name: String) -> List<HtmlElement> =
        { name: String ->
            if (existingNames.contains(name)) cycleElement(name)
            else cycleElementThatDoesNotExist(name)
        }
}
