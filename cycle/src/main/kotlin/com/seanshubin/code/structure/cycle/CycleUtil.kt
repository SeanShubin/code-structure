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

    interface Node{
        fun parts(): List<String>
    }
    data class Single(val value: String) : Node {
        override fun parts(): List<String> {
            return listOf(value)
        }

        override fun toString(): String {
            return value
        }
    }

    data class Cycle(val parts: Set<String>) : Node {
        fun intersects(that:Cycle):Boolean{
            return parts.intersect(that.parts).isNotEmpty()
        }
        fun merge(that:Cycle):Cycle{
            return Cycle(parts + that.parts)
        }
        override fun parts(): List<String> {
            return parts.toList().sorted()
        }

        override fun toString(): String {
            return parts.joinToString(",", "<", ">")
        }

        companion object {
            fun fromParts(parts: List<String>): Cycle {
                return Cycle(parts.toSet())
            }
        }
    }

    data class Accumulator(
        val remainingPaths: List<List<Node>>,
        val exploringPaths: List<List<Node>>,
        val pathsWithCycle: List<List<Node>>,
        val cycles: List<Cycle>
    ) {
        val cyclesByPart = cycles.flatMap { cycle ->
            cycle.parts.map { part ->
                part to cycle
            }
        }.toMap()
        fun process(): Accumulator {
            display("this")
            val a = explorePaths()
            a.display("explorePaths")
            val b = a.findPathsWithCycle()
            b.display("findPathsWithCycle")
            val c = b.convertToCycles()
            c.display("convertToCycles")
            val d = c.replaceWithCycles()
            d.display("replaceWithCycles")
            return d
        }

        private fun display(caption: String) {
            println(caption)
            lines().forEach(::println)
            println()
        }

        private fun explorePaths(): Accumulator {
            val current = remainingPaths.first()
            val newExploringPaths = mutableListOf<List<Node>>()
            val newRemainingPaths = remainingPaths.drop(1).map { next ->
                if (current.last() == next.first()) {
                    val combined: List<Node> = current + next.drop(1)
                    newExploringPaths.add(combined)
                    combined
                } else if(next.last() == current.first()) {
                    val combined: List<Node> = next + current.drop(1)
                    newExploringPaths.add(combined)
                    combined
                } else {
                    next
                }
            }
            return copy(
                exploringPaths = newExploringPaths,
                remainingPaths = newRemainingPaths
            )
        }

        private fun findPathsWithCycle(): Accumulator {
            val pathsWithCycle = exploringPaths.filter(::pathHasCycle)
            return copy(
                exploringPaths = emptyList(),
                pathsWithCycle = pathsWithCycle
            )
        }

        private fun convertToCycles(): Accumulator {
            val newCycles = pathsWithCycle.map(::convertPathToCycle).fold(cycles.toSet(), ::mergeCycle)
            return copy(
                pathsWithCycle = emptyList(),
                cycles = newCycles.toList()
            )
        }

        private fun mergeCycle(existingCycles:Set<Cycle>, newCycle:Cycle):Set<Cycle>{
            var merged = newCycle
            val newCycles = existingCycles.toMutableSet()
            existingCycles.forEach { existingCycle ->
                if(merged.intersects(existingCycle)){
                    merged = merged.merge(existingCycle)
                    newCycles.remove(existingCycle)
                }
            }
            newCycles.add(merged)
            return newCycles
        }

        private fun replaceWithCycles(): Accumulator {
            val newRemainingPaths = remainingPaths.mapNotNull(::updatePathWithCycles)
            return copy(remainingPaths = newRemainingPaths)
        }

        private fun updatePathWithCycles(path: List<Node>): List<Node>? {
            val pathParts = path.flatMap { it.parts() }
            val newPathParts  = mutableListOf<Node>()
            var previousCycle: Cycle? = null
            pathParts.forEach {
                val cycle = cyclesByPart[it]
                if(cycle == null){
                    newPathParts.add(Single(it))
                    previousCycle = null
                } else {
                    if(previousCycle != cycle){
                        newPathParts.add(cycle)
                        previousCycle = cycle
                    }
                }
            }
            if(newPathParts.size > 1) return newPathParts
            else return null
        }

        fun lines(): List<String> {
            val remainingPathsLines = remainingPaths.map {
                it.joinToString(", ", "remainingPath:[", "]")
            }
            val exploringPathsLines = exploringPaths.map {
                it.joinToString(", ", "exploringPaths:[", "]")
            }
            val pathsWithCycleLines = pathsWithCycle.map {
                it.joinToString(", ", "pathsWithCycle:[", "]")
            }
            val cyclesLines = cycles.map {
                "cycle: $it"
            }
            return remainingPathsLines + exploringPathsLines + pathsWithCycleLines + cyclesLines
        }

        companion object {
            fun create(edges: List<Pair<String, String>>): Accumulator {
                val remainingPaths = edges.map { pair ->
                    pair.toList().map {
                        Single(it)
                    }
                }
                return Accumulator(
                    remainingPaths,
                    exploringPaths = emptyList(),
                    pathsWithCycle = emptyList(),
                    cycles = emptyList()
                )
            }

            fun pathHasCycle(path: List<Node>): Boolean {
                return path.distinct().size != path.size
            }

            fun convertPathToCycle(path: List<Node>): Cycle {
                val duplicates = findDuplicates(path)
                val firstIndex = path.indexOfFirst { duplicates.contains(it) }
                val lastIndex = path.indexOfLast { duplicates.contains(it) }
                val partOfPathWithCycle = path.subList(firstIndex, lastIndex + 1)
                val parts = partOfPathWithCycle.flatMap { it.parts() }
                val cycle = Cycle.fromParts(parts)
                return cycle
            }

            fun findDuplicates(path: List<Node>): List<Node> {
                val seen = mutableSetOf<Node>()
                val duplicates = mutableListOf<Node>()
                path.forEach { node ->
                    if (seen.contains(node)) {
                        duplicates.add(node)
                    } else {
                        seen.add(node)
                    }
                }
                return duplicates
            }
        }
    }

    fun prototype(edges: List<Pair<String, String>>): List<Set<String>> {
        var accumulator = Accumulator.create(edges)
        while (accumulator.remainingPaths.isNotEmpty()) {
            val nextAccumulator = accumulator.process()
            if (accumulator == nextAccumulator) {
                throw RuntimeException("No progress")
            }
            accumulator = nextAccumulator
        }
        return accumulator.cycles.map { it.parts }
    }
}
