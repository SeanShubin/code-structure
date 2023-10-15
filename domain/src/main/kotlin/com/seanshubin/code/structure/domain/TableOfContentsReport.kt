package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import java.nio.file.Path

class TableOfContentsReport : Report {
    override fun generate(reportDir: Path, analysis: Analysis): List<CreateFileCommand> {
        val parents = emptyList<Page>()
        val children = listOf(
            Pages.sources,
            Pages.binaries,
            Pages.graph,
            Pages.cycles,
            Pages.local
        )
        val links = children.map(::generateLink)
        val name = "Table Of Contents"
        val html = ReportHelper.wrapInTopLevelHtml(name, links, parents)
        val fileName = "index.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    private fun generateLink(page:Page):HtmlElement {
        val a = anchor(page.name, page.fileName)
        val p = HtmlElement.Tag("p", listOf(a))
        return p
    }
}
