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

    fun <T> bigList(
        list: List<T>,
        toElements: (T) -> List<HtmlElement>,
        className: BigListClassName,
        caption: String?
    ): List<HtmlElement> {
        val sizeElement = if (caption == null) {
            emptyList<HtmlElement>()
        } else {
            listOf(Tag("p", listOf(Text("$caption count: ${list.size}"))))
        }
        val children = list.flatMap(toElements)
        val listElement = Tag(
            "div", children, listOf(
                "class" to className.htmlClassName
            )
        )
        return sizeElement + listOf(listElement)
    }

    fun <T> createTableWithText(
        list: List<T>,
        captions: List<String>,
        elementToRow: (T) -> List<String>,
        caption: String?
    ): List<HtmlElement> {
        val sizeElement = if (caption == null) {
            emptyList<HtmlElement>()
        } else {
            listOf(HtmlElement.Tag("p", listOf(HtmlElement.Text("$caption count: ${list.size}"))))
        }
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
        return sizeElement + listOf(table)
    }

    fun <T> createTableWithElements(
        list: List<T>,
        captions: List<String>,
        elementToRow: (T) -> List<HtmlElement>,
        caption: String?
    ): List<HtmlElement> {
        val sizeElement = if (caption == null) {
            emptyList<HtmlElement>()
        } else {
            listOf(Tag("p", listOf(Text("$caption count: ${list.size}"))))
        }
        val theadCells = captions.map { caption ->
            val theadCell = Text(caption)
            Tag("th", theadCell)
        }
        val theadRow = Tag("tr", theadCells)
        val thead = Tag("thead", listOf(theadRow))
        val valueRows = list.map(elementToRow)
        val tbodyRows = valueRows.map { valueRow ->
            val cells = valueRow.map { element ->
                Tag("td", element)  // No Text() wrapper - already HtmlElement
            }
            Tag("tr", cells)
        }
        val tbody = Tag("tbody", tbodyRows)
        val table = Tag("table", listOf(thead, tbody))
        return sizeElement + listOf(table)
    }
}
