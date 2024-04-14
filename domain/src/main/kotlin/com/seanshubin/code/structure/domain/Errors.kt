package com.seanshubin.code.structure.domain

data class Errors(
    val inDirectCycle: List<String>,
    val inGroupCycle: List<String>,
    val lineage: Lineage
) {
    fun hasErrors(): Boolean =
        inDirectCycle.isNotEmpty()
                || inGroupCycle.isNotEmpty()
                || lineage.ancestorDependsOnDescendant.isNotEmpty()
                || lineage.descendantDependsOnAncestor.isNotEmpty()

    companion object {
        val empty = Errors(
            inDirectCycle = emptyList(),
            inGroupCycle = emptyList(),
            lineage = Lineage.empty
        )
    }
}
