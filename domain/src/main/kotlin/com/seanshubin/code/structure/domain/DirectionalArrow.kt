package com.seanshubin.code.structure.domain

data class DirectionalArrow(
    val inCycle: Set<String>,
    val notInCycle: Set<String>,
    val transitive: Set<String>
) {
    val all: Set<String> get() = inCycle + notInCycle
}
