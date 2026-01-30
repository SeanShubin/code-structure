package com.seanshubin.code.structure.collection

import com.seanshubin.code.structure.collection.MapUtil.addToList
import kotlin.test.Test
import kotlin.test.assertEquals

class MapUtilTest {
    @Test
    fun mergeValuesForDuplicateKeysIntoList() {
        val empty = emptyMap<String, List<Int>>()
        val actual = empty
            .addToList("a" to 1)
            .addToList("b" to 2)
            .addToList("c" to 3)
            .addToList("b" to 4)
            .addToList("b" to 2)

        val expected = mapOf(
            "a" to listOf(1),
            "b" to listOf(2, 4, 2),
            "c" to listOf(3)
        )

        assertEquals(expected, actual)
    }
}
