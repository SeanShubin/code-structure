package com.seanshubin.code.structure.model

data class Arrows(
    val directionOut: Set<String>,
    val directionIn: Set<String>
) {
    companion object {
        val directionOut: (Arrows) -> Set<String> = { it.directionOut }
        val directionIn: (Arrows) -> Set<String> = { it.directionIn }
    }
}

