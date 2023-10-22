package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.Name.isAncestorOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NameTest {
    @Test
    fun isChildOf1() {
        assertFalse("a.b.c".isAncestorOf("a.b.c"))
    }

    @Test
    fun isChildOf2() {
        assertFalse("a.b.c".isAncestorOf("a.b"))
    }

    @Test
    fun isChildOf3() {
        assertTrue("a.b".isAncestorOf("a.b.c"))
    }

    @Test
    fun isChildOf4() {
        assertFalse("a.b.c".isAncestorOf("a"))
    }

    @Test
    fun isChildOf5() {
        assertTrue("a".isAncestorOf("a.b.c"))
    }

    @Test
    fun isChildOf6() {
        assertFalse("a.b".isAncestorOf("a"))
    }

    @Test
    fun isChildOf7() {
        assertTrue("a".isAncestorOf("a.b"))
    }

    @Test
    fun isChildOf8() {
        assertTrue("a".isAncestorOf("b.c"))
    }

    @Test
    fun isChildOf9() {
        assertTrue("a".isAncestorOf("b.c.d"))
    }
}
