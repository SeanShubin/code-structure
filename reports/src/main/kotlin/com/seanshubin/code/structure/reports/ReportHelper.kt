package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.commands.GenerateSvgCommand
import com.seanshubin.code.structure.commands.SubstituteFromFileCommand
import com.seanshubin.code.structure.dot.DotFormat
import com.seanshubin.code.structure.dot.DotNode
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.model.CodeUnit
import java.nio.file.Path

object ReportHelper {
    fun composeGroupPages(groupPath: List<String>): List<Page> {
        val groupPages =
            if (groupPath.isEmpty()) emptyList()
            else groupPages(groupPath.take(groupPath.size - 1))
        return groupPages
    }

    fun groupPage(groupPath: List<String>): Page {
        val codeUnit = CodeUnit(groupPath)
        val caption = codeUnit.caption("Group")
        val id = codeUnit.id("group")
        return Page.createIdCaption(id, caption)
    }

    private fun groupPages(groupPath: List<String>): List<Page> =
        if (groupPath.isEmpty()) listOf(groupPage(groupPath))
        else groupPages(groupPath.take(groupPath.size - 1)) + groupPage(groupPath)

    fun graphCommands(
        reportName: String,
        reportDir: Path,
        baseName: String,
        nodes: List<DotNode>,
        nodeLimitForGraph: Int,
        references: List<Pair<String, String>>,
        cycles: List<List<String>>,
        parents: List<Page>,
        belowGraph: List<HtmlElement> = emptyList(),
        title: String = baseName
    ): List<Command> {
        return if (nodes.size > nodeLimitForGraph) {
            graphCommandsExceedsNodeLimit(
                reportName,
                reportDir,
                baseName,
                nodes,
                nodeLimitForGraph,
                references,
                cycles,
                parents,
                title
            )
        } else {
            graphCommandsWithinNodeLimit(
                reportName,
                reportDir,
                baseName,
                nodes,
                references,
                cycles,
                parents,
                belowGraph,
                title
            )
        }
    }

    private fun graphCommandsWithinNodeLimit(
        reportName: String,
        reportDir: Path,
        baseName: String,
        nodes: List<DotNode>,
        references: List<Pair<String, String>>,
        cycles: List<List<String>>,
        parents: List<Page>,
        belowGraph: List<HtmlElement> = emptyList(),
        title: String
    ): List<Command> {
        val dotSourcePath = reportDir.resolve("$baseName.txt")
        val svgPath = reportDir.resolve("$baseName.svg")
        val htmlTemplatePath = reportDir.resolve("$baseName--template.html")
        val htmlPath = reportDir.resolve("$baseName.html")
        val lines = DotFormat(nodes, references, cycles).toLines()
        val createDotSource = CreateFileCommand(reportName, dotSourcePath, lines)
        val generateSvg = GenerateSvgCommand(reportName, dotSourcePath, svgPath)
        val substitutionTag = "---replace--with--$baseName.svg---"
        val htmlContent = htmlContent(substitutionTag, belowGraph)
        val htmlElement = wrapInTopLevelHtml(title, htmlContent, parents)
        val htmlLines = htmlElement.toLines()
        val createHtml = CreateFileCommand(reportName, htmlTemplatePath, htmlLines)
        val replaceCommand = SubstituteFromFileCommand(reportName, htmlTemplatePath, substitutionTag, svgPath, htmlPath)
        return listOf(createDotSource, generateSvg, createHtml, replaceCommand)
    }

    private fun graphCommandsExceedsNodeLimit(
        reportName: String,
        reportDir: Path,
        baseName: String,
        nodes: List<DotNode>,
        nodeLimitForGraph: Int,
        references: List<Pair<String, String>>,
        cycles: List<List<String>>,
        parents: List<Page>,
        title: String
    ): List<Command> {
        val dotSourcePath = reportDir.resolve("$baseName.txt")
        val htmlTemplatePath = reportDir.resolve("$baseName.html")
        val lines = DotFormat(nodes, references, cycles).toLines()
        val createDotSource = CreateFileCommand(reportName, dotSourcePath, lines)
        val message = "Too many nodes for graph $baseName, limit is $nodeLimitForGraph, have ${nodes.size}"
        val paragraphText = HtmlElement.Text(message)
        val htmlContent = listOf(HtmlElement.Tag("p", paragraphText))
        val htmlElement = wrapInTopLevelHtml(title, htmlContent, parents)
        val htmlLines = htmlElement.toLines()
        val createHtml = CreateFileCommand(reportName, htmlTemplatePath, htmlLines)
        return listOf(createDotSource, createHtml)
    }

    private fun htmlContent(substitutionTag: String, belowGraph: List<HtmlElement>): List<HtmlElement> {
        val divContents = HtmlElement.Text(substitutionTag)
        val div = HtmlElement.Tag("div", divContents)
        return listOf(div) + belowGraph
    }

    fun wrapInTopLevelHtml(name: String, innerContent: List<HtmlElement>, parents: List<Page>): HtmlElement {
        val parentElements = parents.map {
            val uri = it.link
            val a = anchor(it.caption, uri)
            val p = HtmlElement.Tag("p", a)
            p
        }
        val titleText = HtmlElement.Text(name)
        val title = HtmlElement.Tag("title", titleText)
        val resetCss = HtmlElement.Tag(
            "link", attributes = listOf(
                "rel" to "stylesheet",
                "href" to "reset.css"
            )
        )
        val css = HtmlElement.Tag(
            "link", attributes = listOf(
                "rel" to "stylesheet",
                "href" to "code-structure.css"
            )
        )
        val head = HtmlElement.Tag("head", title, resetCss, css)
        val header = HtmlElement.Tag("h1", listOf(HtmlElement.Text(name)))
        val htmlInsideBody = listOf(header) + parentElements + innerContent
        val body = HtmlElement.Tag("body", htmlInsideBody)
        val html = HtmlElement.Tag("html", head, body)
        return html
    }
}
