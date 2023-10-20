package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class GraphReport(private val nodeLimitMainGraph:Int) : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Pages.tableOfContents)
        val analysis = validated.analysis
        return if(analysis.global.names.size > nodeLimitMainGraph){
            exceedsNodeLimit(reportDir, analysis.global)
        } else {
            val nodes = analysis.global.names.map { toDotNode(it, LinkCreator.local) }
            ReportHelper.graphCommands(
                reportDir,
                Pages.graph.id,
                nodes,
                analysis.global.references,
                analysis.global.cycles,
                parents
            )
        }
    }

    fun exceedsNodeLimit(reportDir: Path, analysis: ScopedAnalysis):List<Command> {
        val path = Pages.graph.reportFilePath(reportDir)
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
