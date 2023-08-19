package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement

interface Report {
    val name: String
    fun generate(analysis: Analysis): HtmlElement
}