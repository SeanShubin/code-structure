package com.seanshubin.code.structure.domain

data class Summary(
    val inCycleCount: Int,
    val inGroupCycleCount: Int,
    val ancestorDependsOnDescendantCount: Int,
    val descendantDependsOnAncestorCount: Int
){
    val totalCount = inCycleCount + inGroupCycleCount + ancestorDependsOnDescendantCount + descendantDependsOnAncestorCount
}
