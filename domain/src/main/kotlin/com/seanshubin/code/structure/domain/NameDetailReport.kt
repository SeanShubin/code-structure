package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.nameparser.NameDetail
import java.nio.file.Path

abstract class NameDetailReport : Report {
    abstract val page: Page
    abstract fun lookupSourceFiles(observations: Observations): List<NameDetail>
    override fun generate(baseReportDir: Path, validated: Validated): List<CreateFileCommand> {
        val reportDir = baseReportDir.resolve(category.directory)
        val parents = listOf(Page.tableOfContents)
        val observations = validated.observations
        val htmlInsideBody = generateHtml(observations)
        val html = ReportHelper.wrapInTopLevelHtml(page.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(page.file)
        val lines = html.toLines()
        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private fun generateHtml(observations: Observations): List<HtmlElement> {
        val sourceFiles = lookupSourceFiles(observations)
        return summary(sourceFiles) + table(observations)
    }

    private fun summary(sourceFiles: List<NameDetail>): List<HtmlElement> {
        return listOf(
            Tag("p", Text("source count: ${sourceFiles.size}"))
        )
    }

    private fun table(observations: Observations): List<HtmlElement> {
        val thead = thead()
        val tbody = tbody(observations)
        val table = Tag("table", thead, tbody)
        return listOf(table)
    }

    private fun thead(): HtmlElement {
        val name = Tag("th", Text("location"))
        val modules = Tag("th", Text("name"))
        val row = Tag("tr", name, modules)
        return Tag("thead", row)
    }

    private fun tbody(observations: Observations): HtmlElement {
        val sourcePrefix = observations.sourcePrefix
        val sourceFiles = lookupSourceFiles(observations)
        val rows = sourceFiles.flatMap { sourceDetail ->
            val tdLink = tdLink(sourcePrefix, sourceDetail.path)
            sourceDetail.modules.sorted().map {
                val tdSourceDetail = tdSourceDetail(it)
                Tag("tr", tdLink, tdSourceDetail)
            }
        }
        return Tag("tbody", rows)
    }

    private fun tdLink(sourcePrefix: String, path: Path): HtmlElement {
        val sourceName = path.toString()
        val sourceLink = sourcePrefix + sourceName
        val anchor = anchor(sourceName, sourceLink)
        val td = Tag("td", anchor)
        return td
    }

    private fun tdSourceDetail(name: String): HtmlElement {
        val text = Text(name)
        val td = Tag("td", text)
        return td
    }
}
