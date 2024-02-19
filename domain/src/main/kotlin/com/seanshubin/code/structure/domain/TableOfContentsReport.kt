package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class TableOfContentsReport : Report {
    override fun generate(reportDir: Path, validated: Validated): List<CreateFileCommand> {
        val parents = emptyList<Page>()
        val children = listOf(
            Page.groups,
            Page.entryPoints,
            Page.directCycles,
            Page.groupCycles,
            Page.lineageAncestorDescendant,
            Page.lineageDescendantAncestor,
            Page.codeUnits,
            Page.sources,
            Page.binaries,
            Page.dependencies,
            Page.graph,
            Page.timing
        )
        val listElements = bigList(children, ::generateAnchor, "column-1", caption = null)
        val name = "Table Of Contents"
        val html = ReportHelper.wrapInTopLevelHtml(name, listElements, parents)
        val fileName = "index.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    private fun generateAnchor(page: Page): List<HtmlElement> =
        listOf(anchor(page.caption, page.link))
}
