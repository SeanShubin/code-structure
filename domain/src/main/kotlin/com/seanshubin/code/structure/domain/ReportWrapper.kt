package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement

interface ReportWrapper {
    fun wrapInTopLevelHtml(name: String, htmlInsideBody: List<HtmlElement>): HtmlElement
}
