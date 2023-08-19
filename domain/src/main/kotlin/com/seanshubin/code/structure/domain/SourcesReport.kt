package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement

class SourcesReport : Report {
    override val name: String = "sources"

    override fun generate(analysis: Analysis): HtmlElement {
        throw UnsupportedOperationException("not implemented")
    }
}