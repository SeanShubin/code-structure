package com.seanshubin.code.structure.cycle

interface CycleAlgorithm {
    fun <T> findCycles(edges: Set<Pair<T, T>>): Set<Set<T>>
}
