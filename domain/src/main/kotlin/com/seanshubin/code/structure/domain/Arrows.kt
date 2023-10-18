package com.seanshubin.code.structure.domain

data class Arrows(
    val directionOut: DirectionalArrow,
    val directionIn: DirectionalArrow
) {
    companion object {
        val directionOut: (Arrows) -> DirectionalArrow = { it.directionOut }
        val directionIn: (Arrows) -> DirectionalArrow = { it.directionIn }
    }
}

