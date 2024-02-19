package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class GraphReport(private val nodeLimitMainGraph: Int) : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Page.tableOfContents)
        val analysis = validated.analysis
        return if (analysis.global.names.size > nodeLimitMainGraph) {
            exceedsNodeLimit(reportDir, analysis.global)
        } else {
            val nodes = analysis.global.names.map(::toDotNode)
            ReportHelper.graphCommands(
                reportDir,
                Page.graph.id,
                nodes,
                analysis.global.references,
                analysis.global.cycles,
                parents
            )
        }
    }

    fun exceedsNodeLimit(reportDir: Path, analysis: ScopedAnalysis): List<Command> {
        val path = reportDir.resolve(Page.graph.file)
        val parents = listOf(Page.tableOfContents)
        val paragraphText =
            HtmlElement.Text("Too many nodes for main graph, limit is $nodeLimitMainGraph, have ${analysis.names.size}")
        val content = listOf(HtmlElement.Tag("p", paragraphText))
        val lines = ReportHelper.wrapInTopLevelHtml(Page.graph.caption, content, parents).toLines()
        val createReportCommand = CreateFileCommand(path, lines)
        return listOf(createReportCommand)
    }

    private fun toDotNode(name: String): DotNode =
        DotNode(
            id = name,
            text = name,
            link = name.toCodeUnit().toUriName("local", ".html"),
            color = "blue",
            bold = false
        )
}
