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
            val nodes = localDetail.names.map{toDotNode(it, LinkCreator.local)}
            ReportHelper.graphCommands(
                reportDir,
                "local-$baseName",
                nodes,
                localDetail.references,
                localParents
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
    private fun generateIndex(analysis: Analysis): List<HtmlElement> {
        val children = analysis.names.map { localLink(it) }
        return listOf(bigList(children))
    }

    private fun localLink(name: String): HtmlElement =
        anchor(name, "local-$name.html")
}
