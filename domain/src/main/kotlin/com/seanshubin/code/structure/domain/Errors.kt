package com.seanshubin.code.structure.domain

data class Errors(
    val directCycles: List<String>,
    val groupCycles: List<String>,
    val ancestorDependsOnDescendant: List<Pair<String, String>>,
    val descendantDependsOnAncestor: List<Pair<String, String>>
) {
    fun hasErrors(): Boolean =
        directCycles.isNotEmpty()
                || groupCycles.isNotEmpty()
                || ancestorDependsOnDescendant.isNotEmpty()
                || descendantDependsOnAncestor.isNotEmpty()
}
