package com.seanshubin.code.structure.domain

data class FailConditions(
    val directCycle: Boolean,
    val groupCycle: Boolean,
    val ancestorDependsOnDescendant: Boolean,
    val descendantDependsOnAncestor: Boolean
)
