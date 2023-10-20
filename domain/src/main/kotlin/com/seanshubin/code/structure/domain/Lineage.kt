package com.seanshubin.code.structure.domain

data class Lineage(
    val ancestorToDescendant: List<Pair<String, String>>,
    val descendantToAncestor: List<Pair<String, String>>
)
