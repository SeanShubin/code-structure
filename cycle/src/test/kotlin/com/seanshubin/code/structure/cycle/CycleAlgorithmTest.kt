package com.seanshubin.code.structure.cycle

import kotlin.test.Test
import kotlin.test.assertEquals

class CycleAlgorithmTest {
    @Test
    fun typical() {
        withAlgorithm { algorithm ->
            // given
            val input = setOf(
                "a" to "b",
                "b" to "c",
                "c" to "d",
                "d" to "e",
                "d" to "b"
            )
            val expected = setOf(setOf("b", "c", "d"))

            // when
            val actual = algorithm.findCycles(input, cycleLoopNop)

            // then
            assertEquals(expected, actual)
        }
    }


    @Test
    fun twoCycles() {
        withAlgorithm { algorithm ->
            // given
            val input = setOf(
                "a" to "b",
                "b" to "c",
                "c" to "d",
                "d" to "e",
                "e" to "f",
                "f" to "g",
                "g" to "h",
                "d" to "b",
                "g" to "e"
            )
            val expected = setOf(
                setOf("b", "c", "d"),
                setOf("e", "f", "g")
            )

            // when
            val actual = algorithm.findCycles(input, cycleLoopNop)

            // then
            assertEquals(expected, actual)
        }
    }

    @Test
    fun slightlyComplex() {
        withAlgorithm { algorithm ->
            val input = setOf(
                "c" to "d",
                "h" to "f",
                "a" to "b",
                "e" to "d",
                "j" to "g",
                "d" to "f",
                "b" to "e",
                "g" to "h",
                "b" to "c",
                "h" to "i",
                "d" to "b",
                "f" to "g"
            )

            val expected = setOf(
                setOf("b", "c", "d", "e"),
                setOf("f", "g", "h")
            )

            val actual = algorithm.findCycles(input, cycleLoopNop)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun empty() {
        withAlgorithm { algorithm ->
            // given
            val input = emptySet<Pair<String, String>>()
            val expected = emptySet<Set<String>>()

            // when
            val actual = algorithm.findCycles(input, cycleLoopNop)

            // then
            assertEquals(expected, actual)
        }
    }

    @Test
    fun singleRelation() {
        withAlgorithm { algorithm ->
            // given
            val input = setOf(
                "a" to "b"
            )
            val expected = emptySet<Set<String>>()

            // when
            val actual = algorithm.findCycles(input, cycleLoopNop)

            // then
            assertEquals(expected, actual)
        }
    }

    @Test
    fun simplestCycle() {
        withAlgorithm { algorithm ->
            // given
            val input = setOf(
                "a" to "b",
                "b" to "a"
            )
            val expected = setOf(
                setOf("a", "b")
            )

            // when
            val actual = algorithm.findCycles(input, cycleLoopNop)

            // then
            assertEquals(expected, actual)
        }
    }

    @Test
    fun combineCycles() {
        withAlgorithm { algorithm ->
            // given
            val input = setOf(
                "a" to "b",
                "b" to "a",
                "b" to "c",
                "c" to "b"
            )
            val expected = setOf(
                setOf("a", "b", "c")
            )

            // when
            val actual = algorithm.findCycles(input, cycleLoopNop)

            // then
            assertEquals(expected, actual)
        }
    }

    private val algorithms = listOf(
        CycleAlgorithmCustom(),
        CycleAlgorithmWarshall(),
        CycleAlgorithmTarjan()
    )

    private fun withAlgorithm(f: (CycleAlgorithm) -> Unit) {
        for (algorithm in algorithms) {
            f(algorithm)
        }
    }

    val cycleLoopNop: (Int) -> Unit = { _ -> }
}
