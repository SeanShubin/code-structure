package com.seanshubin.code.structure.cycle

class CycleAlgorithmTarjan : CycleAlgorithm {
    data class Node<T>(val value:T, val nextValues:Set<T>, var id:Int, var cycleId:Int, var inPath:Boolean = false)
    override fun <T> findCycles(edges: Set<Pair<T, T>>, cycleLoop: (Int) -> Unit): Set<Set<T>> {
        val vertices = edges.flatMap { it.toList() }.toSet()
        val grouped = edges.groupBy { it.first }
        val edgeMap = vertices.map{value ->
            val nextValues = grouped[value]?.map { it.second }?.toSet() ?: emptySet()
            value to Node(value, nextValues, -1, -1)
        }.toMap()
        val path = mutableListOf<T>()
        var nextIndex = 0
        val cycles = mutableSetOf<Set<T>>()
        fun visit(current:T){
            val currentNode = edgeMap.getValue(current)
            val currentIndex = nextIndex++
            currentNode.id = currentIndex
            currentNode.cycleId = currentIndex
            path.add(current)
            currentNode.inPath = true
            currentNode.nextValues.forEach { nextValue ->
                val nextNode = edgeMap.getValue(nextValue)
                if(nextNode.id == -1){
                    visit(nextValue)
                    currentNode.cycleId = minOf(currentNode.cycleId, nextNode.cycleId)
                } else if(nextNode.inPath){
                    currentNode.cycleId = minOf(currentNode.cycleId, nextNode.id)
                }
            }
            if(currentNode.id == currentNode.cycleId){
                val cycle = mutableSetOf<T>()
                while(true){
                    val last = path.removeAt(path.size - 1)
                    val lastNode = edgeMap.getValue(last)
                    lastNode.inPath = false
                    cycle.add(last)
                    if(last == current){
                        break
                    }
                }
                if(cycle.size > 1) {
                    cycles.add(cycle)
                }
            }
        }
        edgeMap.keys.forEach {current ->
            cycleLoop(edgeMap.size-nextIndex)
            if(edgeMap.getValue(current).id == -1){
                visit(current)
            }
        }
        return cycles
    }
}