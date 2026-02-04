package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.html.BigListClassName
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import com.seanshubin.code.structure.model.ErrorType
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class TableOfContentsReport : Report {
    override val reportName: String = "table-of-contents"
    override val category: ReportCategory = ReportCategory.BROWSE

    override fun generate(baseReportDir: Path, validated: Validated): List<CreateFileCommand> {
        val reportDir = baseReportDir.resolve(category.directory)
        val parents = emptyList<Page>()
        val children = listOf(
            Page.groups,
            annotateWithNumber(Page.entryPoints, validated.analysis.global.entryPoints.size),
            annotateWithNumber(
                Page.inDirectCycle,
                validated.analysis.summary.errors.getValue(ErrorType.IN_DIRECT_CYCLE).count
            ),
            annotateWithNumber(
                Page.inGroupCycle,
                validated.analysis.summary.errors.getValue(ErrorType.IN_GROUP_CYCLE).count
            ),
            annotateWithNumber(
                Page.lineageAncestorDescendant,
                validated.analysis.summary.errors.getValue(ErrorType.ANCESTOR_DEPENDS_ON_DESCENDANT).count
            ),
            annotateWithNumber(
                Page.lineageDescendantAncestor,
                validated.analysis.summary.errors.getValue(ErrorType.DESCENDANT_DEPENDS_ON_ANCESTOR).count
            ),
            annotateWithNumber(Page.codeUnits, validated.analysis.global.names.size),
            annotateWithNumber(Page.sources, validated.observations.sources.size),
            annotateWithNumber(Page.binaries, validated.observations.binaries.size),
            annotateWithNumber(Page.dependencies, validated.analysis.global.referenceReasons.size),
            Page.graph,
            annotateWithNumber(Page.missingBinaries, validated.observations.missingBinaries.size),
            Page.filterStatistics,
            Page.timing
        )
        val listElements = bigList(children, ::generateAnchor, BigListClassName.COLUMN_1, caption = null)
        val title = "Table Of Contents"
        val html = ReportHelper.wrapInTopLevelHtml(title, listElements, parents)
        val fileName = "index.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return listOf(CreateFileCommand(reportName, path, lines))
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
