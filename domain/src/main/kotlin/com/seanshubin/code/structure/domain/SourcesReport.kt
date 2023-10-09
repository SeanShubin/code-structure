package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.sourceparser.SourceDetail
import java.nio.file.Path

class SourcesReport : HtmlReport() {
    override fun generate(reportDir: Path, analysis: Analysis): List<CreateFileCommand> {
        val name = "Sources"
        val htmlInsideBody = generateHtml(analysis)
        val html = wrapInTopLevelHtml(name, htmlInsideBody)
        val fileName = "sources.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    private fun generateHtml(analysis: Analysis): List<HtmlElement> {
        return summary(analysis) + table(analysis)
    }

    private fun summary(analysis: Analysis): List<HtmlElement> {
        return listOf(
            Tag("p", Text("source count: ${analysis.observations.sourceFiles.size}"))
        )
    }

    private fun table(analysis: Analysis): List<HtmlElement> {
        val thead = thead()
        val tbody = tbody(analysis)
        val table = Tag("table", thead, tbody)
        return listOf(table)
    }

    private fun thead(): HtmlElement {
        val name = Tag("th", Text("location"))
        val modules = Tag("th", Text("name"))
        val row = Tag("tr", name, modules)
        return Tag("thead", row)
    }

    private fun tbody(analysis: Analysis): HtmlElement {
        val inputDir = analysis.observations.inputDir
        val sourcePrefix = analysis.observations.sourcePrefix
        val rows = analysis.observations.sources.map { sourceDetail ->
            val tdLink = tdLink(inputDir, sourcePrefix, sourceDetail.path)
            val tdSourceDetail = tdSourceDetail(sourceDetail)
            Tag("tr", tdLink, tdSourceDetail)
        }
        return Tag("tbody", rows)
    }

    private fun tdLink(inputDir: Path, sourcePrefix: String, path: Path): HtmlElement {
        val sourceName = path.toString()
        val sourceLink = sourcePrefix + sourceName
        val anchor = anchor(sourceName, sourceLink)
        val td = Tag("td", anchor)
        return td
    }

    private fun tdSourceDetail(sourceDetail: SourceDetail): HtmlElement {
        val names = if (sourceDetail.modules.size == 1) {
            sourceDetail.modules[0]
        } else {
            sourceDetail.modules.joinToString(", ", "[", "]")
        }
        val text = Text(names)
        val td = Tag("td", text)
        return td
    }
}
