package com.seanshubin.code.structure.model

data class Lineage(
    val ancestorDependsOnDescendant: List<Pair<String, String>>,
    val descendantDependsOnAncestor: List<Pair<String, String>>
) {
    companion object {
        val empty = Lineage(
            ancestorDependsOnDescendant = emptyList(),
            descendantDependsOnAncestor = emptyList()
        )
    }
}
