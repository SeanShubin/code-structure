package com.seanshubin.code.structure.cycle

object CycleUtil {
    fun <T> findCycles(edges: Set<Pair<T, T>>, cycleLoop: (Int, Int) -> Unit): List<List<T>> {
        val vertices: List<T> = edges.flatMap { listOf(it.first, it.second) }.distinct()
        val adjacencyMatrix: MutableList<MutableList<Boolean>> = vertices.map { row ->
            vertices.map { column ->
                edges.contains(row to column)
            }.toMutableList()
        }.toMutableList()
        val size = vertices.size
        vertices.indices.forEach { k ->
            cycleLoop(k, size)
            vertices.indices.forEach { i ->
                vertices.indices.forEach { j ->
                    if (adjacencyMatrix[i][k] && adjacencyMatrix[k][j]) {
                        adjacencyMatrix[i][j] = true
                    }
                }
            }
        }
        val inCycle = vertices.indices.filter { adjacencyMatrix[it][it] }
        val cycles = inCycle.map { target ->
            val thisCanReachOther = vertices.indices.filter { adjacencyMatrix[target][it] }.toSet()
            val otherCanReachThis = vertices.indices.filter { adjacencyMatrix[it][target] }.toSet()
            val cycleIndices = thisCanReachOther intersect otherCanReachThis
            val cycle = cycleIndices.map { vertices[it] }
            cycle
        }
        return cycles.distinct()
    }

    val cycleLoopNop: (Int, Int) -> Unit = { _, _ -> }
}
