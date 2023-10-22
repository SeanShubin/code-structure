package com.seanshubin.code.structure.collection

import com.seanshubin.code.structure.collection.ListUtil.startsWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListUtilTest {
    @Test
    fun startsWith() {
        assertTrue(listOf(1, 2, 3, 4).startsWith(listOf(1, 2, 3, 4)))
        assertTrue(listOf(1, 2, 3, 4).startsWith(listOf(1, 2, 3)))
        assertTrue(listOf(1, 2, 3, 4).startsWith(listOf(1, 2)))
        assertTrue(listOf(1, 2, 3, 4).startsWith(listOf(1)))
        assertTrue(listOf(1, 2, 3, 4).startsWith(listOf()))
        assertFalse(listOf(1, 2, 3, 4).startsWith(listOf(1, 2, 3, 4, 5)))
        assertFalse(listOf(1, 2, 3, 4).startsWith(listOf(1, 2, 4)))
        assertFalse(listOf(1, 2, 3, 4).startsWith(listOf(2)))
    }

    @Test
    fun commonPrefix() {
        // given
        val list = listOf(
            listOf("aaa", "bbb", "ccc", "ddd", "eee", "fff"),
            listOf("aaa", "bbb", "ccc", "ddd", "eee", "fff"),
            listOf("aaa", "bbb", "ccc", "ddd", "eee", "fff"),
            listOf("aaa", "bbb", "ccc", "aaa", "eee", "fff"),
            listOf("aaa", "bbb", "ccc", "ddd", "eee", "aaa")
        )
        val expected = listOf("aaa", "bbb", "ccc")

        // when
        val actual = ListUtil.commonPrefix(list)

        // then
        assertEquals(expected, actual)
    }
}
