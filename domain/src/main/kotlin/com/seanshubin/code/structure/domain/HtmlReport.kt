package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text

abstract class HtmlReport : Report {
    protected fun wrapInTopLevelHtml(name: String, htmlInsideBody: List<HtmlElement>): HtmlElement {
        val titleText = Text(name)
        val title = Tag("title", titleText)
        val resetCss = Tag(
            "link", attributes = listOf(
                "rel" to "stylesheet",
                "href" to "reset.css"
            )
        )
        val css = Tag(
            "link", attributes = listOf(
                "rel" to "stylesheet",
                "href" to "code-structure.css"
            )
        )
        val head = Tag("head", title, resetCss, css)
        val body = Tag("body", htmlInsideBody)
        val html = Tag("html", head, body)
        return html
    }
}
