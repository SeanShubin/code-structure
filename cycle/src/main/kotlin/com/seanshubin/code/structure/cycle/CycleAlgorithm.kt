package com.seanshubin.code.structure.cycle

interface CycleAlgorithm {
    fun <T> findCycles(edges: Set<Pair<T, T>>, cycleLoop: (Int) -> Unit): Set<Set<T>>
}
