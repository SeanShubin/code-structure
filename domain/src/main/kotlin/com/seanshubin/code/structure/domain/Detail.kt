package com.seanshubin.code.structure.domain

data class Detail(
    val name: String,
    val othersInSameCycle: Set<String>,
    val arrowsOut:Arrows,
    val arrowsIn: Arrows,
    val transitiveOut:Set<String>,
    val transitiveIn:Set<String>
)
