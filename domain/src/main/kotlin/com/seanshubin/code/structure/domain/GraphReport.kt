package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class GraphReport(private val nodeLimitMainGraph:Int) : Report {
    override fun generate(reportDir: Path, analysis: Analysis): List<Command> {
        val parents = listOf(Pages.tableOfContents)
        if(analysis.names.size > nodeLimitMainGraph){
            return exceedsNodeLimit(reportDir, analysis)
        } else {
            val nodes = analysis.names.map { toDotNode(it, LinkCreator.local) }
            return ReportHelper.graphCommands(
                reportDir,
                Pages.graph.id,
                nodes,
                analysis.references,
                parents
            )
        }
    }

    fun exceedsNodeLimit(reportDir: Path, analysis: Analysis):List<Command> {
        val path = reportDir.resolve(Pages.graph.fileName)
        val name = Pages.graph.name
        val parents = listOf(Pages.tableOfContents)
        val paragraphText = HtmlElement.Text("Too many nodes for main graph, limit is $nodeLimitMainGraph, have ${analysis.names.size}")
        val content = listOf(HtmlElement.Tag("p", paragraphText))
        val lines = ReportHelper.wrapInTopLevelHtml(name, content, parents).toLines()
        val createReportCommand = CreateFileCommand(path, lines)
        return listOf(createReportCommand)
    }

    private fun toDotNode(name: String, createLink: (String) -> String): DotNode =
        DotNode(
            id = name,
            text = name,
            link = createLink(name),
            color = "blue",
            bold = false
        )
}
