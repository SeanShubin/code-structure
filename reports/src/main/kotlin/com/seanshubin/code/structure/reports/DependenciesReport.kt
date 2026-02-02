package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import com.seanshubin.code.structure.model.Observations
import com.seanshubin.code.structure.model.Validated
import com.seanshubin.code.structure.relationparser.RelationDetail.Companion.toRelations
import java.nio.file.Path

class DependenciesReport : Report {
    override val reportName: String = "dependencies"
    override val category: ReportCategory = ReportCategory.BROWSE
    override fun generate(baseReportDir: Path, validated: Validated): List<CreateFileCommand> {
        val reportDir = baseReportDir.resolve(category.directory)
        val parents = listOf(Page.tableOfContents)
        val htmlInsideBody = generateHtml(validated.observations)
        val html = ReportHelper.wrapInTopLevelHtml(Page.dependencies.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(Page.dependencies.file)
        val lines = html.toLines()
        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private fun generateHtml(observations: Observations): List<HtmlElement> {
        return summary(observations) + table(observations)
    }

    private fun table(observations: Observations): List<HtmlElement> {
        val thead = thead()
        val tbody = tbody(observations)
        val captionText = Page.dependencies.caption
        val caption = Tag("h2", Text(captionText))
        val table = Tag("table", thead, tbody)
        return listOf(caption, table)
    }

    private fun summary(observations: Observations): List<HtmlElement> {
        return listOf(
            Tag("p", Text("relation count: ${observations.binaries.toRelations().size}"))
        )
    }

    private fun thead(): HtmlElement {
        val name = Tag("th", Text("name"))
        val dependency = Tag("th", Text("dependency"))
        val elements = listOf(name, dependency)
        val row = Tag("tr", elements)
        return Tag("thead", row)
    }

    private fun tbody(observations: Observations): HtmlElement {
        val rows = observations.binaries.toRelations().map { relation ->
            binaryRowWithDependencies(relation)
        }
        return Tag("tbody", rows)
    }

    private fun binaryRowWithDependencies(relation: Pair<String, String>): HtmlElement {
        val (name, dependencyName) = relation
        val binaryCells = binaryCellsWithDependencies(name, dependencyName)
        val binaryRow = Tag("tr", binaryCells)
        return binaryRow
    }

    private fun binaryCellsWithDependencies(name: String, dependencyName: String): List<HtmlElement> {
        return listOf(
            binaryCell(name),
            binaryCell(dependencyName)
        )
    }

    private fun binaryCell(text: String): HtmlElement =
        Tag("td", Text(text))
}
