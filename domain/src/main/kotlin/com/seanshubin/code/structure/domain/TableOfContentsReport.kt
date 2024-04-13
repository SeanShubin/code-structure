package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.BigListClassName
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class TableOfContentsReport : Report {
    override fun generate(reportDir: Path, validated: Validated): List<CreateFileCommand> {
        val parents = emptyList<Page>()
        val children = listOf(
            Page.groups,
            annotateWithNumber(Page.entryPoints, validated.analysis.global.entryPoints.size),
            annotateWithNumber(Page.directCycles, validated.analysis.errors.inDirectCycle.size),
            annotateWithNumber(Page.groupCycles, validated.analysis.errors.inGroupCycle.size),
            annotateWithNumber(
                Page.lineageAncestorDescendant,
                validated.analysis.errors.ancestorDependsOnDescendant.size
            ),
            annotateWithNumber(
                Page.lineageDescendantAncestor,
                validated.analysis.errors.descendantDependsOnAncestor.size
            ),
            annotateWithNumber(Page.codeUnits, validated.analysis.global.names.size),
            annotateWithNumber(Page.sources, validated.observations.sources.size),
            annotateWithNumber(Page.binaries, validated.observations.binaries.size),
            annotateWithNumber(Page.dependencies, validated.analysis.global.references.size),
            Page.graph,
            annotateWithNumber(Page.missingBinaries, validated.observations.missingBinaries.size),
            Page.timing
        )
        val listElements = bigList(children, ::generateAnchor, BigListClassName.COLUMN_1, caption = null)
        val name = "Table Of Contents"
        val html = ReportHelper.wrapInTopLevelHtml(name, listElements, parents)
        val fileName = "index.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    private fun generateAnchor(page: Page): List<HtmlElement> =
        listOf(anchor(page.caption, page.link))

    private fun annotateWithNumber(page: Page, value: Int): Page = object : Page {
        override val caption: String get() = if (value == 0) page.caption else "${page.caption} ($value)"
        override val link: String get() = page.link
        override val file: String get() = page.file
        override val id: String get() = page.id
    }
}
