package com.seanshubin.code.structure.domain

data class DetailExceptTransitive(
    val name: String,
    val othersInSameCycle: Set<String>,
    val arrowsOut: Arrows,
    val arrowsIn: Arrows
)
