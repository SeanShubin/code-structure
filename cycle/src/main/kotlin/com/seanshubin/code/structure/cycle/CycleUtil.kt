package com.seanshubin.code.structure.cycle

object CycleUtil {
    fun <T> findCycles_(edges: Set<Pair<T, T>>, cycleLoop: (Int, Int) -> Unit): Set<Set<T>> {
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
            cycle.toSet()
        }.toSet()
        return cycles
    }

    fun <T> findCycles(edges: Set<Pair<T, T>>, cycleLoop: (Int, Int) -> Unit):Set<Set<T>> {
        val cycleList = mutableListOf<List<T>>()
        val foundCycle:(List<T>)->Unit = { path:List<T> -> cycleList.add(path)}
        val edgeMap = edges.groupBy { it.first }.map { (first, second) ->
            first to second.map { it.second }.toSet()
        }.toMap()
        edges.map {
            depthFirst(edgeMap, listOf(it.first), foundCycle)
        }
        val cycleSet = cycleList.map{it.toSet()}.toSet()
        val mergedCycleSet = mergeCycles(cycleSet)
        return mergedCycleSet
    }

    private fun <T> mergeCycles(cycles:Set<Set<T>>):Set<Set<T>> {
        val remainingCycles = mutableSetOf<Set<T>>()
        val result = mutableSetOf<Set<T>>()
        remainingCycles.addAll(cycles)
        while(remainingCycles.isNotEmpty()){
            val first = remainingCycles.first()
            val rest = remainingCycles.drop(1)
            val toMerge = rest.filter { first.intersect(it).isNotEmpty()}
            val merged = toMerge.fold(first){acc, next -> acc.union(next)}
            remainingCycles.remove(first)
            toMerge.forEach { remainingCycles.remove(it) }
            result.add(merged)
        }
        return result
    }

    private fun <T> depthFirst(edgeMap: Map<T, Set<T>>, path: List<T>, foundCycle: (List<T>) -> Unit) {
        val endOfPath: T = path.last()
        val nextDirections: Set<T> = edgeMap[endOfPath] ?: emptySet()
        nextDirections.forEach{nextDirection ->
            val index = path.indexOf(nextDirection)
            if(index == -1){
                depthFirst(edgeMap, path + nextDirection, foundCycle)
            }else {
                foundCycle(path.drop(index))
            }
        }
    }

    val cycleLoopNop: (Int, Int) -> Unit = { _, _ -> }
}
