package com.seanshubin.code.structure.utility.stateless

import kotlin.test.Test
import kotlin.test.assertEquals

class ListUtilTest {
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
