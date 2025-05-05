package com.seanshubin.code.structure.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DynamicUtilTest {
    @Test
    fun simpleOperations() {
        val path = listOf("a", "b", "c")
        val value = 123
        val before = null
        val after = DynamicUtil.set(before, path, value)
        assertFalse(DynamicUtil.exists(before, path))
        assertTrue(DynamicUtil.exists(after, path))
        assertEquals(value, DynamicUtil.get(after, path))
    }

    @Test
    fun exists(){
        val value = mapOf("a" to 1)
        assertFalse(DynamicUtil.exists(value, listOf("b")))
    }

    @Test
    fun flattenListAt() {
        val input = mapOf(
            "a" to 1,
            "b" to mapOf("c" to listOf("d", "e", "f"))
        )
        val expected1 = mapOf(
            "a" to 1,
            "b" to mapOf(
                "c" to "d"
            )
        )
        val expected2 = mapOf(
            "a" to 1,
            "b" to mapOf(
                "c" to "e"
            )
        )
        val expected3 = mapOf(
            "a" to 1,
            "b" to mapOf(
                "c" to "f"
            )
        )
        val expected = listOf(expected1, expected2, expected3)
        val path = listOf("b", "c")
        val actual = DynamicUtil.flattenListAt(input, path)
        assertEquals(expected, actual)
    }

    @Test
    fun flattenListWithIndex() {
        val input = mapOf(
            "a" to 1,
            "b" to mapOf("c" to listOf("d", "e", "f"))
        )
        val expected1 = mapOf(
            "a" to 1,
            "b" to mapOf(
                "c" to mapOf(
                    "value" to "d",
                    "index" to 0
                )
            )
        )
        val expected2 = mapOf(
            "a" to 1,
            "b" to mapOf(
                "c" to mapOf(
                    "value" to "e",
                    "index" to 1
                )
            )
        )
        val expected3 = mapOf(
            "a" to 1,
            "b" to mapOf(
                "c" to mapOf(
                    "value" to "f",
                    "index" to 2
                )
            )
        )
        val expected = listOf(expected1, expected2, expected3)
        val path = listOf("b", "c")
        val actual = DynamicUtil.flattenListWithIndex(input, path, "value", "index")
        assertEquals(expected, actual)
    }

    @Test
    fun flattenMap() {
        val input = mapOf("a" to mapOf("b" to "c"), "d" to mapOf("e" to "f"))
        val expected = mapOf("a.b" to "c", "d.e" to "f")
        val combineKeys = { a: Any?, b: Any? -> "$a.$b" }
        val actual = DynamicUtil.flattenMap(input, combineKeys)
        assertEquals(expected, actual)
    }

    @Test
    fun flattenMap2() {
        val input = mapOf("a" to mapOf("b" to mapOf("c" to 1)), "d" to mapOf("e" to mapOf("f" to 2)))
        val expected = mapOf("a.b.c" to 1, "d.e.f" to 2)
        val combineKeys = { a: Any?, b: Any? -> "$a.$b" }
        val actual = DynamicUtil.flattenMap(input, combineKeys)
        assertEquals(expected, actual)
    }

    @Test
    fun flattenMap3() {
        val input = mapOf(
            "a" to listOf(1, 2),
            "b" to listOf(3, 4)
        )
        val expected = mapOf(
            "a.0" to 1,
            "a.1" to 2,
            "b.0" to 3,
            "b.1" to 4
        )
        val combine = { a: Any?, b: Any? -> "$a.$b" }
        val actual = DynamicUtil.flattenMap(input, combine)
        assertEquals(expected, actual)
    }

    @Test
    fun update() {
        val increment = { a: Any? ->
            a as Int
            a + 1
        }
        val path = listOf("a", "b", "c")
        val expected = mapOf("a" to mapOf("b" to mapOf("c" to 3)))
        val initial = null
        val first = DynamicUtil.update(initial, path, 0, increment)
        val second = DynamicUtil.update(first, path, 0, increment)
        val third = DynamicUtil.update(second, path, 0, increment)
        assertEquals(expected, third)
    }

    @Test
    fun typeHistogram() {
        val o = mapOf("a" to listOf(1, "a", 'b', 2.34, null, false), "b" to true, "c" to null, "d" to 1.23, "e" to 123)
        val expected = mapOf(
            "a.0" to mapOf("Integer" to 1),
            "a.1" to mapOf("String" to 1),
            "a.2" to mapOf("Character" to 1),
            "a.3" to mapOf("Double" to 1),
            "a.4" to mapOf("Nothing?" to 1),
            "a.5" to mapOf("Boolean" to 1),
            "b" to mapOf("Boolean" to 1),
            "c" to mapOf("Nothing?" to 1),
            "d" to mapOf("Double" to 1),
            "e" to mapOf("Integer" to 1)
        )
        val actual = DynamicUtil.typeHistogram(o)
        assertEquals(expected, actual)
    }

    @Test
    fun accumulateTypes() {
        val a = mapOf("a" to 1, "b" to mapOf("c" to true, "d" to 123), "c" to listOf("e", 1.23))
        val b = mapOf("a" to 1, "b" to mapOf("d" to 123), "c" to listOf("e", 1.23), "d" to mapOf("e" to "f"))
        val c = mapOf("a" to "1", "b" to mapOf("c" to null, "d" to 123), "c" to listOf(2.34))
        val list = listOf(a, b, c)
        val expected = mapOf(
            "a" to mapOf(
                "Integer" to 2,
                "String" to 1
            ),
            "b.c" to mapOf(
                "Boolean" to 1,
                "Nothing?" to 1
            ),
            "b.d" to mapOf(
                "Integer" to 3
            ),
            "c.0" to mapOf(
                "String" to 2,
                "Double" to 1
            ),
            "c.1" to mapOf(
                "Double" to 2
            ),
            "d.e" to mapOf(
                "String" to 1
            ),
        )
        val actual = list.fold(emptyMap(), DynamicUtil::accumulateTypeHistogram)
        assertEquals(expected, actual)
    }

    @Test
    fun setArray() {
        val path = listOf("a", 2, "b", 3)
        val arrayDefaults = listOf(-1, -2)
        val before = null
        val after = DynamicUtil.setWithArrays(before, path, "c", arrayDefaults)
        val actual = DynamicUtil.get(after, path)
        val expectedValue = "c"
        val expected = mapOf("a" to listOf(-1, -1, mapOf("b" to listOf(-2, -2, -2, "c"))))
        assertEquals(expectedValue, actual)
        assertEquals(expected, after)
    }

    @Test
    fun updateArray() {
        val path = listOf("a", 2, "b", 3)
        val arrayDefaults = listOf(-1, -2)
        val before = mapOf("a" to listOf(1, 2, mapOf("b" to listOf(3, 4, 5, "target", 6, 7)), 8, 9))
        val after = DynamicUtil.setWithArrays(before, path, "replacement", arrayDefaults)
        val actual = DynamicUtil.get(after, path)
        val expectedValue = "replacement"
        val expected = mapOf("a" to listOf(1, 2, mapOf("b" to listOf(3, 4, 5, "replacement", 6, 7)), 8, 9))
        assertEquals(expectedValue, actual)
        assertEquals(expected, after)
    }
}
