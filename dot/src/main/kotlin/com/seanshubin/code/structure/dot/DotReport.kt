package com.seanshubin.code.structure.dot

data class DotReport(
    val names: List<String>,
    val references: List<Pair<String, String>>
) {
    fun toLines(): List<String> {
        val nameLines = names.map(::toNameLine)
        val referenceLines = references.map(::toReferenceLine)
        val nameAndReferenceLines = nameLines + referenceLines
        val allLines = wrapInDigraph("detangled", nameAndReferenceLines)
        return allLines
    }

    private fun wrapInDigraph(caption: String, lines: List<String>): List<String> {
        val header =listOf("digraph $caption {")
        val body = listOf("bgcolor=lightgray") + lines
        val indentedBody = body.map {"  $it" }
        val footer= listOf("}")
        return header + indentedBody + footer
    }

    private fun toNameLine(name: String): String = "\"$name\""
    private fun toReferenceLine(reference: Pair<String, String>): String {
        val (first, second) = reference
        return "\"$first\" -> \"$second\""
    }
}
