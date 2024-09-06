package com.seanshubin.code.structure.cycle

object CycleUtil {
    fun <T> findCycles(edges: Set<Pair<T, T>>): Set<Set<T>> {
        var edgeMap: Map<Set<T>, Set<Set<T>>> = edges.groupBy { it.first }.map { (first, second) ->
            setOf(first) to second.map { setOf(it.second) }.toSet()
        }.toMap()
        var currentCycle = searchForCycle(edgeMap, emptyList())
        while (currentCycle != null) {
            edgeMap = mergeCycle(edgeMap, currentCycle)
            currentCycle = searchForCycle(edgeMap, emptyList())
        }
        return edgeMap.keys.filter { it.size > 1 }.toSet()
    }

    private fun <T> searchForCycle(edgeMap: Map<Set<T>, Set<Set<T>>>, path: List<Set<T>>): Set<Set<T>>? {
        edgeMap.keys.forEach { key ->
            val result = searchForCycle(edgeMap, key, path)
            if(result != null){
                return result
            }
        }
        return null
    }

    private fun <T> searchForCycle(edgeMap: Map<Set<T>, Set<Set<T>>>, key: Set<T>, path: List<Set<T>>): Set<Set<T>>? {
        val values = edgeMap[key] ?: emptySet()
        values.forEach { value ->
            val index = path.indexOf(value)
            if(index != -1 ){
                return (path.drop(index) + listOf(key)).toSet()
            } else {
                val cycle = searchForCycle(edgeMap, value, path + listOf(key))
                if(cycle != null ) {
                    return cycle
                }
            }
        }
        return null
    }

    private fun <T> mergeCycle(edgeMap: Map<Set<T>, Set<Set<T>>>, cycle: Set<Set<T>>): Map<Set<T>, Set<Set<T>>> {
//        println("")
//        println("mergeCycle")
//        edgeMap.forEach{(key, value) ->
//            println("edgeMap: $key -> $value")
//        }
//        println("cycle: $cycle")
        val newKey:Set<T> = cycle.flatten().toSet()
//        println("newKey: $newKey")
        val newValue:Set<Set<T>> = cycle.map { edgeMap.getValue(it) }.flatten().filterNot{cycle.contains(it)}.toSet()
        val updateEntry:(Map.Entry<Set<T>, Set<Set<T>>>)->Pair<Set<T>, Set<Set<T>>>? = {(first, second) ->
            if(cycle.contains(first)) {
                null
            } else {
                val filteredSecond:Set<Set<T>> = second.filterNot { cycle.contains(it) }.toSet()
                val toAdd = if((second intersect cycle).isEmpty()) {
                    emptySet()
                } else {
                    setOf(newKey)
                }
                val newSecond:Set<Set<T>> = filteredSecond + toAdd
                first to newSecond
            }
        }
//        println("newValue: $newValue")
        val newEntry:Pair<Set<T>, Set<Set<T>>> = newKey to newValue
//        println("newEntry: $newEntry")
        val filteredEdgeMap:Map<Set<T>, Set<Set<T>>> = edgeMap.mapNotNull(updateEntry).toMap()
//        filteredEdgeMap.forEach{(key, value) ->
//            println("filteredEdgeMap: $key -> $value")
//        }
        val newEdgeMap:Map<Set<T>, Set<Set<T>>> = filteredEdgeMap + newEntry
//        newEdgeMap.forEach{(key, value) ->
//            println("newEdgeMap: $key -> $value")
//        }
        return newEdgeMap
    }
}
