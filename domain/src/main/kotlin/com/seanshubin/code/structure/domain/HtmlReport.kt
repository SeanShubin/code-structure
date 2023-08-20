package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import java.nio.file.Path

abstract class HtmlReport : Report {
    override fun generate(reportDir: Path, analysis: Analysis): CreateFileCommand {
        val htmlInsideBody = generateHtml(analysis)
        val html = wrapInTopLevelHtml(name, htmlInsideBody)
        val fileName = "$name.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return CreateFileCommand(path, lines)
    }

    abstract fun generateHtml(analysis: Analysis): List<HtmlElement>
    private fun wrapInTopLevelHtml(name: String, htmlInsideBody: List<HtmlElement>): HtmlElement {
        val titleText = Text(name)
        val title = Tag("title", titleText)
        val resetCss = Tag(
            "link", attributes = listOf(
                "rel" to "stylesheet",
                "href" to "reset.css"
            )
        )
        val css = Tag(
            "link", attributes = listOf(
                "rel" to "stylesheet",
                "href" to "code-structure.css"
            )
        )
        val head = Tag("head", title, resetCss, css)
        val body = Tag("body", htmlInsideBody)
        val html = Tag("html", head, body)
        return html
    }
}
