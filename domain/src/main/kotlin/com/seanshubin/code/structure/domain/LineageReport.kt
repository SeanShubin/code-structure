package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.html.BigListClassName
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class LineageReport(
    private val page: Page,
    private val direction: (Lineage) -> List<Pair<String, String>>
) : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Page.tableOfContents)
        val htmlInsideBody = generateHtml(validated.analysis.lineage)
        val html = ReportHelper.wrapInTopLevelHtml(page.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(page.file)
        val lines = html.toLines()
        val topCommand = CreateFileCommand(path, lines)
        return listOf(topCommand)
    }

    private fun generateHtml(lineage: Lineage): List<HtmlElement> {
        return lineageElement(direction(lineage))
    }

    private fun lineageElement(references: List<Pair<String, String>>): List<HtmlElement> {
        val captionElement = HtmlElement.Tag("h2", listOf(HtmlElement.Text(page.caption)))
        val referencesElement = bigList(references, ::referenceToElements, BigListClassName.COLUMN_2, page.caption)
        return listOf(captionElement) + referencesElement
    }

    private fun referenceToElements(reference: Pair<String, String>): List<HtmlElement> =
        listOf(nameToElement(reference.first), nameToElement(reference.second))

    private fun nameToElement(name: String): HtmlElement {
        val link = name.toCodeUnit().toUriName("local", ".html")
        val anchor = anchor(name, link)
        val inSpan = listOf(anchor)
        val span = HtmlElement.Tag("span", inSpan)
        return span
    }
}
