package com.seanshubin.code.structure.domain

data class Cycle(val parts: List<Name>) {
    val size: Int get() = parts.size
}

