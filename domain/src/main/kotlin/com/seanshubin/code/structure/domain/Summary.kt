package com.seanshubin.code.structure.domain

data class Summary(
    val directCycleCount: Int,
    val groupCycleCount: Int,
    val ancestorDependsOnDescendantCount: Int,
    val descendantDependsOnAncestorCount: Int
)
