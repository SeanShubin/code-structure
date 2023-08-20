package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text

class SourcesReport : HtmlReport() {
    override val name: String = "sources"
    override fun generateHtml(analysis: Analysis): List<HtmlElement> {
        val thead = thead()
        val tbody = tbody(analysis)
        val table = Tag("table", thead, tbody)
        return listOf(table)
    }

    private fun thead(): HtmlElement {
        val text = Text("name")
        val header = Tag("th", text)
        val row = Tag("tr", header)
        return Tag("thead", row)
    }

    private fun tbody(analysis: Analysis): HtmlElement {
        val inputDir = analysis.observations.inputDir
        val sourcePrefix = analysis.observations.sourcePrefix
        val rows = analysis.observations.sourceFiles.map {
            val relativePath = inputDir.relativize(it)
            val sourceName = relativePath.toString()
            val sourceLink = sourcePrefix + sourceName
            val anchor = anchor(sourceName, sourceLink)
            val td = Tag("td", anchor)
            Tag("tr", td)
        }
        return Tag("tbody", rows)
    }

    private fun anchor(value: String, link: String): HtmlElement =
        Tag(
            "a", children = listOf(Text(value)), attributes = listOf(
                "href" to link
            )
        )
}
