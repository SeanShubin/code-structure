package com.seanshubin.code.structure.domain

data class Errors(
    val inDirectCycle: List<String>,
    val inGroupCycle: List<String>,
    val ancestorDependsOnDescendant: List<Pair<String, String>>,
    val descendantDependsOnAncestor: List<Pair<String, String>>
) {
    fun hasErrors(): Boolean =
        inDirectCycle.isNotEmpty()
                || inGroupCycle.isNotEmpty()
                || ancestorDependsOnDescendant.isNotEmpty()
                || descendantDependsOnAncestor.isNotEmpty()
    companion object {
        val empty = Errors(
            inDirectCycle = emptyList(),
            inGroupCycle = emptyList(),
            ancestorDependsOnDescendant = emptyList(),
            descendantDependsOnAncestor = emptyList()
        )
    }
}
