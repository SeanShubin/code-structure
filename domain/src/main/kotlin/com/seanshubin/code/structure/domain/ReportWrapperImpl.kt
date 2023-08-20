package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement

class ReportWrapperImpl : ReportWrapper {
    override fun wrapInTopLevelHtml(name: String, htmlInsideBody: List<HtmlElement>): HtmlElement {
        val titleText = HtmlElement.Text(name)
        val title = HtmlElement.Tag("title", titleText)
        val resetCss = HtmlElement.Tag(
            "link", attributes = listOf(
                "rel" to "stylesheet",
                "href" to "reset.css"
            )
        )
        val css = HtmlElement.Tag(
            "link", attributes = listOf(
                "rel" to "stylesheet",
                "href" to "code-structure.css"
            )
        )
        val head = HtmlElement.Tag("head", title, resetCss, css)
        val body = HtmlElement.Tag("body", htmlInsideBody)
        val html = HtmlElement.Tag("html", head, body)
        return html
    }
}
