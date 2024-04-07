package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class CodeUnitTest {
    @Test
    fun isChildOf1() {
        verify(expected = false, "a.b.c", "a.b.c")
    }

    @Test
    fun isChildOf2() {
        verify(expected = false, "a.b.c", "a.b")
    }

    @Test
    fun isChildOf3() {
        verify(expected = true, "a.b", "a.b.c")
    }

    @Test
    fun isChildOf4() {
        verify(expected = false, "a.b.c", "a")
    }

    @Test
    fun isChildOf5() {
        verify(expected = true, "a", "a.b.c")
    }

    @Test
    fun isChildOf6() {
        verify(expected = false, "a.b", "a")
    }

    @Test
    fun isChildOf7() {
        verify(expected = true, "a", "a.b")
    }

    @Test
    fun isChildOf8() {
        verify(expected = true, "a", "b.c")
    }

    @Test
    fun isChildOf9() {
        verify(expected = true, "a", "b.c.d")
    }

    private fun verify(expected: Boolean, first: String, second: String) {
        assertEquals(expected, first.toCodeUnit().isAncestorOf(second.toCodeUnit()))
    }
}
