package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import com.seanshubin.code.structure.relationparser.RelationDetail
import java.nio.file.Path

class BinariesReport : Report {
    override val name: String = "binaries"

    override fun generate(reportDir: Path, validated: Validated): List<CreateFileCommand> {
        val parents = listOf(Page.tableOfContents)
        val htmlInsideBody = generateHtml(validated.observations)
        val html = ReportHelper.wrapInTopLevelHtml(Page.binaries.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(Page.binaries.file)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    private fun generateHtml(observations: Observations): List<HtmlElement> {
        return summary(observations) + table(observations)
    }

    private fun table(observations: Observations): List<HtmlElement> {
        val thead = thead()
        val tbody = tbody(observations)
        val captionText = Page.binaries.caption
        val caption = Tag("h2", Text(captionText))
        val table = Tag("table", thead, tbody)
        return listOf(caption, table)
    }

    private fun summary(observations: Observations): List<HtmlElement> {
        return listOf(
            Tag("p", Text("binary count: ${observations.binaries.size}"))
        )
    }

    private fun thead(): HtmlElement {
        val name = Tag("th", Text("name"))
        val location = Tag("th", Text("location"))
        val elements = listOf(name, location)
        val row = Tag("tr", elements)
        return Tag("thead", row)
    }

    private fun tbody(observations: Observations): HtmlElement {
        val rows = observations.binaries.map { binary ->
            binaryRowWithoutDependencies(binary)
        }
        return Tag("tbody", rows)
    }

    private fun binaryRowWithoutDependencies(binary: RelationDetail): HtmlElement {
        val binaryCells = binaryCellsWithoutDependencies(binary)
        val binaryRow = Tag("tr", binaryCells)
        return binaryRow
    }

    private fun binaryCellsWithoutDependencies(binary: RelationDetail): List<HtmlElement> {
        return listOf(
            binaryCell(binary.name),
            binaryCell(binary.location),
        )
    }

    private fun binaryCell(text: String): HtmlElement =
        Tag("td", Text(text))
}
