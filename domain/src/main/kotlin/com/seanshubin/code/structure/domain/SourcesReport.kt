package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.nameparser.NameDetail
import java.nio.file.Path

class SourcesReport : Report {
    override fun generate(reportDir: Path, validated: Validated): List<CreateFileCommand> {
        val parents = listOf(Page.tableOfContents)
        val observations = validated.observations
        val htmlInsideBody = generateHtml(observations)
        val html = ReportHelper.wrapInTopLevelHtml(Page.sources.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(Page.sources.file)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    private fun generateHtml(observations: Observations): List<HtmlElement> {
        return summary(observations) + table(observations)
    }

    private fun summary(observations: Observations): List<HtmlElement> {
        return listOf(
            Tag("p", Text("source count: ${observations.sourceFiles.size}"))
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
        val rows = observations.sources.map { sourceDetail ->
            val tdLink = tdLink(sourcePrefix, sourceDetail.path)
            val tdSourceDetail = tdSourceDetail(sourceDetail)
            Tag("tr", tdLink, tdSourceDetail)
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

    private fun tdSourceDetail(nameDetail: NameDetail): HtmlElement {
        val names = if (nameDetail.modules.size == 1) {
            nameDetail.modules[0]
        } else {
            nameDetail.modules.joinToString(", ", "[", "]")
        }
        val text = Text(names)
        val td = Tag("td", text)
        return td
    }
}
