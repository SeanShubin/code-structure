package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil

object ReportUtil {
    private val localPrefix = "local"
    fun toLocalBaseName(name:String):String {
        return name.toCodeUnit().id(localPrefix)
    }
    fun toLocalUri(codeUnit:CodeUnit): String {
        return codeUnit.toUriName(localPrefix, ".html")
    }
    fun toLocalUri(name:String): String {
        return toLocalUri(name.toCodeUnit())
    }
    fun toLocalLink(name:String): HtmlElement {
        val link = toLocalUri(name)
        return HtmlElementUtil.anchor(name, link)
    }
}
