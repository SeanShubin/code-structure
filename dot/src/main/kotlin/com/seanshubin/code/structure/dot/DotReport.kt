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
        val dquoteName = name.dquote()
        val dquoteColor = "blue".dquote()
        val dquoteUri = uri.dquote()
        return "$dquoteName [fontcolor=$dquoteColor URL=$dquoteUri]"
    }

    private fun toReferenceLine(reference: Pair<String, String>): String {
        val (first, second) = reference
        val firstDQuote = first.dquote()
        val secondDQuote = second.dquote()
        return "$firstDQuote -> $secondDQuote"
    }

    private fun String.dquote(): String = "\"$this\""
}
