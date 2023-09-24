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
}
