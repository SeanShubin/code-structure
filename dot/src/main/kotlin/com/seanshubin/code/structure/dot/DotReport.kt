package com.seanshubin.code.structure.dot

data class DotReport(
    val nodes: List<DotNode>,
    val references: List<Pair<String, String>>
) {
    fun toLines(): List<String> {
        val nodeLines = nodes.map(::toNodeLine)
        val referenceLines = references.map(::toReferenceLine)
        val nameAndReferenceLines = nodeLines + referenceLines
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

    private fun toNodeLine(node: DotNode): String =
        DotNodeModel(node.id, listOf(
            "fontcolor" to node.color,
            "URL" to node.link,
            "label" to node.text
        )).toDotLine()

    private fun toReferenceLine(reference: Pair<String, String>): String {
        val (first, second) = reference
        val firstDQuote = first.quote()
        val secondDQuote = second.quote()
        return "$firstDQuote -> $secondDQuote"
    }

    private fun String.quote(): String = "\"$this\""
}
