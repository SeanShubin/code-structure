package com.seanshubin.code.structure.html

//
// This file was imported from: ../kotlin-reusable
// Module: html
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

interface HtmlElement {
    fun toLines(): List<String>

    data class Tag(
        val name: String,
        val children: List<HtmlElement> = emptyList(),
        val attributes: List<Pair<String, String>> = emptyList()
    ) : HtmlElement {
        constructor(name: String, vararg children: HtmlElement) : this(name, children.toList())

        private fun openTag(): String =
            if (attributes.isEmpty()) {
                "<$name>"
            } else {
                val attributesString = attributes.joinToString(" ") { (attrName, value) ->
                    "$attrName=\"$value\""
                }
                "<$name $attributesString>"
            }

        private fun closeTag(): String = "</$name>"

        override fun toLines(): List<String> {
            val first = openTag()
            val last = closeTag()
            val middle = children.flatMap { it.toLines() }.map { "  $it" }
            return listOf(first) + middle + listOf(last)
        }
    }

    data class Text(
        val lines: List<String>
    ) : HtmlElement {
        constructor(line: String) : this(listOf(line))

        override fun toLines(): List<String> = lines
    }

    companion object {
        fun tagText(tag: String, text: String): HtmlElement {
            return Tag(tag, Text(text))
        }
    }
}
