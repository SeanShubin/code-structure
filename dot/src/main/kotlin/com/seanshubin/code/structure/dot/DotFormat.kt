package com.seanshubin.code.structure.dot

data class DotFormat(
    val nodes: List<DotNode>,
    val references: List<Pair<String, String>>,
    val cycles: List<List<String>>
) {
    fun toLines(): List<String> {
        val nodeLines = nodes.map(::toNodeLine)
        val cycleReferenceList = cycles.map { cycle ->
            references.filter { (first, second) ->
                cycle.contains(first) && cycle.contains(second)
            }
        }
        val cycleReferences = cycleReferenceList.flatten()
        val nonCycleReferences = references.filterNot{cycleReferences.contains(it)}
        val referenceLines = nonCycleReferences.map(::toReferenceLine)
        val cycleLines = cycleReferenceList.flatMapIndexed(::toCycleContainerLines)
        val nameAndReferenceLines = nodeLines + referenceLines + cycleLines
        val allLines = wrapInDigraph(nameAndReferenceLines)
        return allLines
    }

    private fun toCycleContainerLines(index: Int, references: List<Pair<String, String>>): List<String> {
        val referenceLines = references.map{(first, second) ->
            "${first.quote()} -> ${second.quote()}"
        }
        val cycleLines = listOf("penwidth=2", "pencolor=Red") + referenceLines
        return listOf("subgraph cluster_$index {") + cycleLines.map { "  $it" } + listOf("}")
    }

    private fun wrapInDigraph(lines: List<String>): List<String> {
        val header = listOf("digraph detangled {")
        val body = listOf("bgcolor=lightgray") + lines
        val indentedBody = body.map { "  $it" }
        val footer = listOf("}")
        return header + indentedBody + footer
    }

    private fun toNodeLine(node: DotNode): String {
        val colorAttribute: List<Pair<String, String>> = if(node.link == null) emptyList() else listOf("fontcolor" to node.color)
        val urlAttribute: List<Pair<String, String>> = if(node.link == null) emptyList() else listOf("URL" to node.link)
        val labelAttribute: List<Pair<String, String>> = listOf("label" to node.text)
        val boldAttribute: List<Pair<String, String>> = if (node.bold) {
            listOf("style" to "bold")
        } else {
            emptyList()
        }
        val attributes = colorAttribute + urlAttribute + labelAttribute + boldAttribute
        return DotNodeModel(node.id, attributes).toDotLine()
    }

    private fun toReferenceLine(reference: Pair<String, String>): String {
        val (first, second) = reference
        val firstDQuote = first.quote()
        val secondDQuote = second.quote()
        return "$firstDQuote -> $secondDQuote"
    }

    private fun String.quote(): String = "\"$this\""
}
