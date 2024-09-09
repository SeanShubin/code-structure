package com.seanshubin.code.structure.cycle

import com.seanshubin.code.structure.cycle.CycleAlgorithmCustom.Node.Companion.connectTo

class CycleAlgorithmCustom : CycleAlgorithm {
    override fun <T> findCycles(edges: Set<Pair<T, T>>, cycleLoop: (Int) -> Unit): Set<Set<T>> {
        var accumulator = Accumulator.create(edges)
        while (accumulator.remainingPaths.isNotEmpty()) {
            cycleLoop(accumulator.remainingPaths.size)
            accumulator = accumulator.process()
        }
        return accumulator.cycles.map { it.parts }.toSet()
    }

    interface Node<T> {
        fun parts(): Set<T>

        companion object {
            fun <T> List<Node<T>>.connectTo(that: List<Node<T>>): List<Node<T>>? {
                return connectTo(that, 1)
            }

            private fun <T> List<Node<T>>.connectTo(that: List<Node<T>>, connectorSize: Int): List<Node<T>>? {
                if (size < connectorSize) return null
                if (that.size < connectorSize) return null
                val thisConnector = subList(size - connectorSize, size)
                val thatConnector = that.subList(0, connectorSize)
                return if (thisConnector == thatConnector) {
                    this + that.subList(connectorSize, that.size)
                } else {
                    connectTo(that, connectorSize + 1)
                }
            }
        }
    }

    data class Single<T>(val value: T) : Node<T> {
        override fun parts(): Set<T> {
            return setOf(value)
        }

        companion object {
            fun <T> makePath(parts: List<T>): List<Node<T>> {
                return parts.map(::Single)
            }
        }
    }

    data class Cycle<T>(val parts: Set<T>) : Node<T> {
        fun intersects(that: Cycle<T>): Boolean {
            return parts.intersect(that.parts).isNotEmpty()
        }

        fun merge(that: Cycle<T>): Cycle<T> {
            return Cycle(parts + that.parts)
        }

        override fun parts(): Set<T> {
            return parts
        }

        override fun toString(): String {
            return parts.joinToString(",", "<", ">")
        }

        companion object {
            fun <T> fromParts(parts: List<T>): Cycle<T> {
                return Cycle(parts.toSet())
            }
        }
    }

    data class Accumulator<T>(
        val remainingPaths: List<List<Node<T>>>,
        val exploringPaths: List<List<Node<T>>>,
        val pathsWithCycle: List<List<Node<T>>>,
        val cycles: Set<Cycle<T>>
    ) {
        val cyclesByPart = cycles.flatMap { cycle ->
            cycle.parts.map { part ->
                part to cycle
            }
        }.toMap()

        fun process(): Accumulator<T> {
            val a = explorePaths()
            return a.handleCycles()
        }

        private fun handleCycles(): Accumulator<T> {
            var a = findPathsWithCycle()
            while (a.pathsWithCycle.isNotEmpty()) {
                val b = a.convertToCycles()
                val c = b.replaceWithCycles()
                a = c.findAllPathsWithCycle()
            }
            return a
        }

        private fun explorePaths(): Accumulator<T> {
            val current = remainingPaths.first()
            val remainingMinusCurrent = remainingPaths.drop(1)
            val newExploringPaths = remainingMinusCurrent.mapNotNull { next ->
                val currentToNext = current.connectTo(next)
                if (currentToNext != null) {
                    currentToNext
                } else {
                    val nextToCurrent = next.connectTo(current)
                    if (nextToCurrent != null) {
                        nextToCurrent
                    } else {
                        null
                    }
                }
            }.distinct()
            val newRemainingPaths = remainingMinusCurrent + newExploringPaths
            return copy(
                exploringPaths = newExploringPaths,
                remainingPaths = newRemainingPaths.distinct()
            )
        }

        private fun findPathsWithCycle(): Accumulator<T> {
            val pathsWithCycle = exploringPaths.filter(::pathHasCycle)
            return copy(
                exploringPaths = emptyList(),
                pathsWithCycle = pathsWithCycle
            )
        }

        private fun findAllPathsWithCycle(): Accumulator<T> {
            val pathsWithCycle = remainingPaths.filter(::pathHasCycle)
            return copy(
                exploringPaths = emptyList(),
                pathsWithCycle = pathsWithCycle
            )
        }

        private fun convertToCycles(): Accumulator<T> {
            val newCycles = pathsWithCycle.map(::convertPathToCycle).fold(cycles.toSet(), ::mergeCycle)
            return copy(
                pathsWithCycle = emptyList(),
                cycles = newCycles
            )
        }

        private fun mergeCycle(existingCycles: Set<Cycle<T>>, newCycle: Cycle<T>): Set<Cycle<T>> {
            var merged = newCycle
            val newCycles = existingCycles.toMutableSet()
            existingCycles.forEach { existingCycle ->
                if (merged.intersects(existingCycle)) {
                    merged = merged.merge(existingCycle)
                    newCycles.remove(existingCycle)
                }
            }
            newCycles.add(merged)
            return newCycles
        }

        private fun replaceWithCycles(): Accumulator<T> {
            val newRemainingPaths = remainingPaths.mapNotNull(::updatePathWithCycles)
            return copy(remainingPaths = newRemainingPaths.distinct())
        }

        private fun updatePathWithCycles(path: List<Node<T>>): List<Node<T>>? {
            val pathParts = path.flatMap { it.parts() }
            val newPathParts = mutableListOf<Node<T>>()
            var previousCycle: Cycle<T>? = null
            pathParts.forEach {
                val cycle = cyclesByPart[it]
                if (cycle == null) {
                    newPathParts.add(Single(it))
                    previousCycle = null
                } else {
                    if (previousCycle != cycle) {
                        newPathParts.add(cycle)
                        previousCycle = cycle
                    }
                }
            }
            if (newPathParts.size > 1) return newPathParts
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
            fun <T> create(edges: Set<Pair<T, T>>): Accumulator<T> {
                val remainingPaths = edges.map { pair ->
                    pair.toList().map {
                        Single(it)
                    }
                }
                return Accumulator(
                    remainingPaths,
                    exploringPaths = emptyList(),
                    pathsWithCycle = emptyList(),
                    cycles = emptySet()
                )
            }

            fun <T> pathHasCycle(path: List<Node<T>>): Boolean {
                return path.distinct().size != path.size
            }

            fun <T> convertPathToCycle(path: List<Node<T>>): Cycle<T> {
                val duplicates = findDuplicates(path)
                val firstIndex = path.indexOfFirst { duplicates.contains(it) }
                val lastIndex = path.indexOfLast { duplicates.contains(it) }
                val partOfPathWithCycle = path.subList(firstIndex, lastIndex + 1)
                val parts = partOfPathWithCycle.flatMap { it.parts() }
                val cycle = Cycle.fromParts(parts)
                return cycle
            }

            fun <T> findDuplicates(path: List<Node<T>>): List<Node<T>> {
                val seen = mutableSetOf<Node<T>>()
                val duplicates = mutableListOf<Node<T>>()
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
}