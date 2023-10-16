package com.seanshubin.code.structure.domain

data class Detail(
    val name: String,
    val othersInSameCycle: Set<String>,
    val arrowsOut: Arrows,
    val arrowsIn: Arrows,
    val transitiveOut: Set<String>,
    val transitiveIn: Set<String>
) {
    val cycleIncludingThis: Set<String> get() = if (othersInSameCycle.isEmpty()) emptySet() else othersInSameCycle + name

    companion object {
        val directionOut: (Detail) -> Arrows = { it.arrowsOut }
        val directionIn: (Detail) -> Arrows = { it.arrowsIn }
    }
}
