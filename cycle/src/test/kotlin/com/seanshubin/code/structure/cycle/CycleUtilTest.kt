package com.seanshubin.code.structure.cycle

import kotlin.test.Test
import kotlin.test.assertEquals

class CycleUtilTest {
    @Test
    fun typical() {
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
        val actual = CycleUtil.findCycles(input)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun twoCycles() {
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
        val actual = CycleUtil.findCycles(input)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun repeatProblem() {
        // given
        val input = setOf(
            "ModuleP" to "PathP",
            "PathA" to "PathC",
            "PathC" to "PathE",
            "PathE" to "ModuleE",
            "PathE" to "PathG",
            "PathG" to "PathC",
            "PathG" to "PathI",
            "PathI" to "PathP",
            "PathP" to "PathR",
            "PathR" to "PathI",
            "PathR" to "PathT",
        )
        val expected = setOf(
            setOf("PathC", "PathE", "PathG"),
            setOf("PathI", "PathP", "PathR")
        )

        // when
        val actual = CycleUtil.findCycles(input)

        // then
        assertEquals(expected, actual)
    }
}
