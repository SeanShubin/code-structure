package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text

class SourcesReport : Report {
    override val name: String = "sources"

    override fun generate(analysis: Analysis): List<HtmlElement> {
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
        val rows = analysis.observations.sourceFiles.map {
            val text = Text(it.toString())
            val td = Tag("td", text)
            Tag("tr", td)
        }
        return Tag("tbody", rows)
    }
}