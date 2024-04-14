package com.seanshubin.code.structure.html

interface HtmlElement {
    fun toLines(): List<String>
    data class Tag(
        val tag: String,
        val children: List<HtmlElement> = emptyList(),
        val attributes: List<Pair<String, String>> = emptyList()
    ) : HtmlElement {
        constructor(tag: String, vararg children: HtmlElement) : this(tag, children.toList())

        private fun openTag(): String =
            if (attributes.isEmpty()) {
                "<$tag>"
            } else {
                val attributesString = attributes.joinToString(" ") { (name, value) ->
                    "$name=\"$value\""
                }
                "<$tag $attributesString>"
            }

        private fun closeTag(): String = "</$tag>"
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
            val textElement = Text(text)
            val tagElement = Tag(tag, listOf(textElement))
            return tagElement
        }
    }
}
