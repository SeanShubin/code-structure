package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class TableOfContentsReport : Report {
    override fun generate(reportDir: Path, analysis: Analysis): List<CreateFileCommand> {
        val parents = emptyList<Page>()
        val children = listOf(
            Pages.sources,
            Pages.binaries,
            Pages.graph,
            Pages.cycles,
            Pages.local,
            Pages.timing
        )
        val listElements = children.map(::generateAnchor)
        val listElement = bigList(listElements)
        val name = "Table Of Contents"
        val html = ReportHelper.wrapInTopLevelHtml(name, listOf(listElement), parents)
        val fileName = "index.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    private fun generateAnchor(page: Page): HtmlElement =
        anchor(page.name, page.fileName)
}
