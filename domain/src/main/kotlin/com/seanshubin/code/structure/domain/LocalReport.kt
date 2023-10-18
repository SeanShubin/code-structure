package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class LocalReport(private val localDepth: Int) : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Pages.tableOfContents)
        val path = reportDir.resolve(Pages.local.fileName)
        val analysis = validated.analysis
        val content = bigList(analysis.names, ::localLink, "local")
        val graphs = generateGraphs(reportDir, analysis, parents)
        val lines = ReportHelper.wrapInTopLevelHtml(Pages.local.name, content, parents).toLines()
        val index = CreateFileCommand(path, lines)
        val commands = listOf(index) + graphs
        return commands
    }

    private fun generateGraphs(reportDir: Path, analysis: Analysis, parents: List<Page>): List<Command> =
        analysis.names.flatMap { baseName ->
            val localNamesSet = expand(setOf(baseName), analysis, localDepth)
            val localNamesSorted = localNamesSet.toList().sorted()
            val localParents = parents + listOf(Pages.local)
            val nodes = localNamesSorted.map { toDotNode(baseName, it, analysis, LinkCreator.local) }
            val referencesSet = analysis.referencesForScope(localNamesSet)
            val referencesSorted = referencesSet.sortedWith(Analysis.referenceComparator)
            ReportHelper.graphCommands(
                reportDir,
                "local-$baseName",
                nodes,
                referencesSorted,
                localParents
            )
        }

    private tailrec fun expand(names: Set<String>, analysis: Analysis, times: Int): Set<String> {
        if (times == 0) return names
        val expandedNames = expandOnce(names, analysis)
        return expand(expandedNames, analysis, times - 1)
    }

    private fun expandOnce(names: Set<String>, analysis: Analysis): Set<String> {
        val namesOut = outerShell(names, analysis, Arrows.directionOut)
        val namesIn = outerShell(names, analysis, Arrows.directionIn)
        return names + namesOut + namesIn
    }

    private fun outerShell(names: Set<String>, analysis: Analysis, direction: (Arrows) -> DirectionalArrow): Set<String> =
        names.map { analysis.detailByName.getValue(it) }.flatMap { direction(it.arrows).all }.toSet()

    private fun toDotNode(baseName: String, name: String, analysis: Analysis, createLink: (String) -> String): DotNode {
        val baseDetail = analysis.detailByName.getValue(baseName)
        val bold = name == baseName
        val cycle = baseDetail.cycle ?: emptySet()
        val isCycle = cycle.contains(name)
        val text = if (isCycle) {
            "↻ $name ↻ (${cycle.size})"
        } else {
            name
        }
        return DotNode(
            id = name,
            text = text,
            link = createLink(name),
            color = "blue",
            bold = bold
        )
    }

    private fun localLink(name: String): HtmlElement =
        anchor(name, "local-$name.html")
}
