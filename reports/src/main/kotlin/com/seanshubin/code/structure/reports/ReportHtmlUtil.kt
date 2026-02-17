package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text

object ReportHtmlUtil {
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
}
