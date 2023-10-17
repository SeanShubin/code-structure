package com.seanshubin.code.structure.html

import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text

object HtmlElementUtil {
    fun anchor(title: String, link: String): HtmlElement =
        Tag(
            tag = "a",
            children = listOf(Text(title)),
            attributes = listOf("href" to link)
        )

    fun <T> bigList(list: List<T>, toElement: (T) -> HtmlElement): HtmlElement {
        val children = list.map(toElement)
        return Tag(
            "div", children, listOf(
                "class" to "big-list"
            )
        )
    }

    fun <T> createTable(list: List<T>, captions: List<String>, elementToRow: (T) -> List<String>): HtmlElement {
        val theadCells = captions.map { caption ->
            val theadCell = Text(caption)
            Tag("th", theadCell)
        }
        val theadRow = Tag("tr", theadCells)
        val theadRows = listOf(theadRow)
        val thead = Tag("thead", theadRows)
        val valueRows = list.map(elementToRow)
        val tbodyRows = valueRows.map { valueRow ->
            val cells = valueRow.map { value ->
                val text = Text(value)
                Tag("td", text)
            }
            Tag("tr", cells)
        }
        val tbody = Tag("tbody", tbodyRows)
        val table = Tag("table", listOf(thead, tbody))
        return table
    }
}
