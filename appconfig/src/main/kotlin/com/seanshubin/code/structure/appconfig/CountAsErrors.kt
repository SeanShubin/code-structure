package com.seanshubin.code.structure.appconfig

data class CountAsErrors(
    val inDirectCycle: Boolean,
    val inGroupCycle: Boolean,
    val ancestorDependsOnDescendant: Boolean,
    val descendantDependsOnAncestor: Boolean
)
