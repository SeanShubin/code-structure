package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.dot.DotReport
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil
import java.nio.file.Path

object ReportHelper {
    fun graphCommands(
        reportDir: Path,
        baseName: String,
        names: List<String>,
        references: List<Pair<String, String>>
    ): List<Command> {
        val dotSourcePath = reportDir.resolve("$baseName.txt")
        val svgPath = reportDir.resolve("$baseName.svg")
        val htmlTemplatePath = reportDir.resolve("$baseName--template.html")
        val htmlPath = reportDir.resolve("$baseName.html")
        val lines = DotReport(names, references).toLines()
        val createDotSource = CreateFileCommand(dotSourcePath, lines)
        val generateSvg = GenerateSvgCommand(dotSourcePath, svgPath)
        val substitutionTag = "---replace--with--$baseName.svg---"
        val htmlContent = htmlContent(substitutionTag)
        val htmlElement = wrapInTopLevelHtmlWithParent(baseName, htmlContent)
        val htmlLines = htmlElement.toLines()
        val createHtml = CreateFileCommand(htmlTemplatePath, htmlLines)
        val replaceCommand = SubstituteFromFileCommand(htmlTemplatePath, substitutionTag, svgPath, htmlPath)
        return listOf(createDotSource, generateSvg, createHtml, replaceCommand)
    }

    private fun htmlContent(substitutionTag: String): List<HtmlElement> {
        val divContents = HtmlElement.Text(substitutionTag)
        val div = HtmlElement.Tag("div", divContents)
        return listOf(div)
    }

    fun wrapInTopLevelHtmlWithParent(name: String, innerContent: List<HtmlElement>): HtmlElement {
        val parentLink = listOf(HtmlElementUtil.anchor(title = "table of contents", "index.html"))
        return wrapInTopLevelHtml(name, innerContent, parentLink)
    }
    fun wrapInTopLevelHtmlWithoutParent(name: String, innerContent: List<HtmlElement>): HtmlElement {
        return wrapInTopLevelHtml(name, innerContent, emptyList())
    }
    private fun wrapInTopLevelHtml(name: String, innerContent: List<HtmlElement>, parentLink:List<HtmlElement>): HtmlElement {
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
        val htmlInsideBody = listOf(header) + parentLink + innerContent
        val body = HtmlElement.Tag("body", htmlInsideBody)
        val html = HtmlElement.Tag("html", head, body)
        return html
    }
}
