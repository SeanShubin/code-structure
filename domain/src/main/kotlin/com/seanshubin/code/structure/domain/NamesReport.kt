package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ComparatorUtil.pairComparator
import com.seanshubin.code.structure.domain.Name.toGroupPath
import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class NamesReport(private val localDepth: Int) : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Page.tableOfContents)
        val path = reportDir.resolve(Page.names.file)
        val analysis = validated.analysis
        val localLink = renderFunction(localDepth)
        val content = bigList(analysis.global.names, localLink, "big-list", "local")
        val graphs = if(localDepth == 0) emptyList() else generateGraphs(reportDir, analysis, parents)
        val lines = ReportHelper.wrapInTopLevelHtml(Page.names.caption, content, parents).toLines()
        val index = CreateFileCommand(path, lines)
        val commands = listOf(index) + graphs
        return commands
    }

    private fun renderFunction(localDepth:Int):(String)->List<HtmlElement> =
        if(localDepth == 0){
            ::containingGroupLink
        } else {
            ::localLink
        }

    private fun containingGroupLink(name:String):List<HtmlElement> {
        val group = name.toGroupPath()
        val containingGroup = group.dropLast(1)
        val linkText = containingGroup.joinToString("-", "group-", ".html")
        return listOf(anchor(name, linkText))
    }

    private fun localLink(name:String):List<HtmlElement> =
        listOf(anchor(name, "local-$name.html"))

    private fun generateGraphs(reportDir: Path, analysis: Analysis, inheritedParents: List<Page>): List<Command> =
        analysis.global.names.flatMap { baseName ->
            val localNamesSet = expand(setOf(baseName), analysis.global, localDepth)
            val localNamesSorted = localNamesSet.toList().sorted()
            val localParents = appendSourceLink(inheritedParents + listOf(Page.names), baseName, analysis)
            val nodes = localNamesSorted.map { toDotNode(baseName, it, analysis.global, LinkCreator.local) }
            val referencesSet = analysis.global.referencesForScope(localNamesSet)
            val referencesSorted = referencesSet.sortedWith(pairComparator)
            ReportHelper.graphCommands(
                reportDir,
                "local-$baseName",
                nodes,
                referencesSorted,
                emptyList(),
                localParents
            )
        }

    private fun appendSourceLink(currentParents: List<Page>, name: String, analysis: Analysis): List<Page> {
        val sourceLink = analysis.lookupUri(name)
        val page = Page.createCaptionLink("Source", sourceLink)
        return currentParents + page
    }

    private tailrec fun expand(names: Set<String>, analysis: ScopedAnalysis, times: Int): Set<String> {
        if (times == 0) return names
        val expandedNames = expandOnce(names, analysis)
        return expand(expandedNames, analysis, times - 1)
    }

    private fun expandOnce(names: Set<String>, analysis: ScopedAnalysis): Set<String> {
        val namesOut = outerShell(names, analysis, Arrows.directionOut)
        val namesIn = outerShell(names, analysis, Arrows.directionIn)
        return names + namesOut + namesIn
    }

    private fun outerShell(
        names: Set<String>,
        analysis: ScopedAnalysis,
        direction: (Arrows) -> DirectionalArrow
    ): Set<String> =
        names.map { analysis.lookupDetail(it) }.flatMap { direction(it.arrows).all }.toSet()

    private fun toDotNode(
        baseName: String,
        name: String,
        analysis: ScopedAnalysis,
        createLink: (String) -> String
    ): DotNode {
        val baseDetail = analysis.lookupDetail(baseName)
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
}
