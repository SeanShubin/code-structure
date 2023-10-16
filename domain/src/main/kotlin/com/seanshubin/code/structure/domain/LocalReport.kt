package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class LocalReport : Report {
    override fun generate(reportDir: Path, analysis: Analysis): List<Command> {
        val parents = listOf(Pages.tableOfContents)
        val path = reportDir.resolve(Pages.local.fileName)
        val content = generateIndex(analysis)
        val graphs = generateGraphs(reportDir, analysis, parents)
        val lines = ReportHelper.wrapInTopLevelHtml(Pages.local.name, content, parents).toLines()
        val index = CreateFileCommand(path, lines)
        val commands = listOf(index) + graphs
        return commands
    }

    private fun generateGraphs(reportDir: Path, analysis: Analysis, parents: List<Page>): List<Command> =
        analysis.names.flatMap { baseName ->
            val localDetail = analysis.localDetail.getValue(baseName)
            val localParents = parents + listOf(Pages.local)
            val nodes = localDetail.names.map{toDotNode(baseName, it, analysis, LinkCreator.local)}
            ReportHelper.graphCommands(
                reportDir,
                "local-$baseName",
                nodes,
                localDetail.references,
                localParents
            )
        }

    private fun toDotNode(baseName:String, name: String, analysis:Analysis, createLink: (String) -> String): DotNode {
        val baseDetail = analysis.detail.getValue(baseName)
        val detail = analysis.detail.getValue(name)
        val bold = name == baseName
        val cycle = baseDetail.cycleIncludingThis
        val isCycle = cycle.contains(name)
        val text = if(isCycle){
            "↻ $name ↻ (${cycle.size})"
        } else {
            if(baseDetail.arrowsOut.notInCycle.contains(name)) {
                val size = detail.transitiveOut.size+1
                "$name ($size)"
            } else if(baseDetail.arrowsIn.notInCycle.contains(name)){
                val size = detail.transitiveIn.size+1
                "$name ($size)"
            } else if(baseName == name) {
                name
            } else {
                throw RuntimeException("$name is not relevant to local report on $baseName")
            }
        }
        return DotNode(
            id = name,
            text = text,
            link = createLink(name),
            color = "blue",
            bold = bold
        )
    }
    private fun generateIndex(analysis: Analysis): List<HtmlElement> {
        val children = analysis.names.map { localLink(it) }
        return listOf(bigList(children))
    }

    private fun localLink(name: String): HtmlElement =
        anchor(name, "local-$name.html")
}
