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
        val expected = listOf(listOf("b", "c", "d"))

        // when
        val actual = CycleUtil.findCycles(input, CycleUtil.cycleLoopNop)

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
        val expected = listOf(
            listOf("b", "c", "d"),
            listOf("e", "f", "g")
        )

        // when
        val actual = CycleUtil.findCycles(input, CycleUtil.cycleLoopNop)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun prototype(){
        val input = listOf(
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

        val expected = listOf(
            setOf("b", "c", "d", "e"),
            setOf("f", "g", "h")
        )

        val actual = CycleUtil.prototype(input)

        assertEquals(expected, actual)
    }

    @Test
    fun prototypeA(){
        // given
        val input = emptyList<Pair<String, String>>()
        val expected = emptyList<Set<String>>()

        // when
        val actual = CycleUtil.prototype(input)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun prototypeB(){
        // given
        val input = listOf(
            "a" to "b"
        )
        val expected = emptyList<Set<String>>()

        // when
        val actual = CycleUtil.prototype(input)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun prototypeC(){
        // given
        val input = listOf(
            "a" to "b",
            "b" to "a"
        )
        val expected = listOf(
            setOf("a", "b")
        )

        // when
        val actual = CycleUtil.prototype(input)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun prototypeCombineCycles(){
        // given
        val input = listOf(
            "a" to "b",
            "b" to "a",
            "b" to "c",
            "c" to "b"
        )
        val expected = listOf(
            setOf("a", "b", "c")
        )

        // when
        val actual = CycleUtil.prototype(input)

        // then
        assertEquals(expected, actual)
    }
}
