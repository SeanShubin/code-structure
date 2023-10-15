package com.seanshubin.code.structure.dot

data class DotReport(
    val names: List<String>,
    val references: List<Pair<String, String>>,
    val nameToUri: (String) -> String
) {
    fun toLines(): List<String> {
        val nameLines = names.map(::toNameLine)
        val referenceLines = references.map(::toReferenceLine)
        val nameAndReferenceLines = nameLines + referenceLines
        val allLines = wrapInDigraph(nameAndReferenceLines)
        return allLines
    }

    private fun wrapInDigraph(lines: List<String>): List<String> {
        val header = listOf("digraph detangled {")
        val body = listOf("bgcolor=lightgray") + lines
        val indentedBody = body.map { "  $it" }
        val footer = listOf("}")
        return header + indentedBody + footer
    }

    private fun toNameLine(name: String): String {
        val uri = nameToUri(name)
        val quoteName = name.quote()
        val quoteColor = "blue".quote()
        val quoteUri = uri.quote()
        return "$quoteName [fontcolor=$quoteColor URL=$quoteUri]"
    }

    private fun toReferenceLine(reference: Pair<String, String>): String {
        val (first, second) = reference
        val firstDQuote = first.quote()
        val secondDQuote = second.quote()
        return "$firstDQuote -> $secondDQuote"
    }

    private fun String.quote(): String = "\"$this\""
}
