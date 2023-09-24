package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor

abstract class HtmlReport : Report {
    protected fun wrapInTopLevelHtml(name: String, innerContent: List<HtmlElement>): HtmlElement {
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
        val htmlInsideBody = header(name) + parentLink() + innerContent
        val body = Tag("body", htmlInsideBody)
        val html = Tag("html", head, body)
        return html
    }

    private fun header(name:String):List<HtmlElement> = listOf(
        Tag("h1", listOf(Text(name))),
    )

    protected open fun parentLink(): List<HtmlElement> =
        listOf(anchor(title = "table of contents", "index.html"))
}
