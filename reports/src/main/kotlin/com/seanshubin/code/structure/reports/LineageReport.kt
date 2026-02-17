package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.reports.ReportHtmlUtil.bigList
import com.seanshubin.code.structure.model.Lineage
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class LineageReport(
    private val page: Page,
    private val direction: (Lineage) -> List<Pair<String, String>>
) : Report {
    override val reportName: String = "lineage"
    override val category: ReportCategory = ReportCategory.BROWSE
    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val parents = listOf(Page.tableOfContents)
        val htmlInsideBody = generateHtml(validated)
        val html = ReportHelper.wrapInTopLevelHtml(page.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(page.file)
        val lines = html.toLines()
        val topCommand = CreateFileCommand(reportName, path, lines)
        return listOf(topCommand)
    }

    private fun generateHtml(validated: Validated): List<HtmlElement> {
        val lineage: List<Pair<String, String>> = direction(validated.analysis.lineage)
        return lineageElement(lineage)
    }

    private fun lineageElement(references: List<Pair<String, String>>): List<HtmlElement> {
        val captionElement = HtmlElement.Tag("h2", listOf(HtmlElement.Text(page.caption)))
        val referencesElement = bigList(references, ::referenceToElements, BigListClassName.COLUMN_2, page.caption)
        return listOf(captionElement) + referencesElement
    }

    private fun referenceToElements(reference: Pair<String, String>): List<HtmlElement> =
        listOf(nameToElement(reference.first), nameToElement(reference.second))

    private fun nameToElement(name: String): HtmlElement {
        val span = HtmlElement.tagText("span", name)
        return span
    }
}
