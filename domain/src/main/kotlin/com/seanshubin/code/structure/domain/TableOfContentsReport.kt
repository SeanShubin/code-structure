package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import java.nio.file.Path

class TableOfContentsReport : HtmlReport() {
    override fun generate(reportDir: Path, analysis: Analysis): List<CreateFileCommand> {
        val name = "Table Of Contents"
        val htmlInsideBody = generateHtml()
        val html = wrapInTopLevelHtml(name, htmlInsideBody)
        val fileName = "index.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    override fun parentLink(): List<HtmlElement> = emptyList()

    private fun generateHtml(): List<HtmlElement> =
        listOf(
            link("sources"),
            link("binaries"),
            link("cycles")
        )

    private fun link(name: String): HtmlElement =
        Tag("p", anchor(name, "$name.html"))
}
