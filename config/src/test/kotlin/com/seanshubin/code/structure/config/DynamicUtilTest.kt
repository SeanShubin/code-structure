package com.seanshubin.code.structure.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DynamicUtilTest {
    @Test
    fun setValueAtPath() {
        assertEquals(
            mapOf("a" to mapOf("b" to 3)),
            DynamicUtil.setValueAtPath(listOf("a", "b"), null, 3)
        )
        assertEquals(
            mapOf("a" to mapOf("b" to 3)),
            DynamicUtil.setValueAtPath(listOf("a", "b"), mapOf("a" to mapOf<String, Any?>()), 3)
        )
        assertEquals(
            mapOf(
                "a" to mapOf("b" to 3),
                "e" to 4
            ),
            DynamicUtil.setValueAtPath(listOf("a", "b"), mapOf("e" to 4), 3)
        )
        assertEquals(
            mapOf(
                "a" to mapOf(
                    "b" to 3,
                    "f" to 5
                )
            ),
            DynamicUtil.setValueAtPath(listOf("a", "b"), mapOf("a" to mapOf("f" to 5)), 3)
        )
        assertEquals(
            mapOf(
                "a" to mapOf(
                    "b" to 3,
                    "g" to 7
                ),
                "c" to 6
            ),
            DynamicUtil.setValueAtPath(listOf("a", "b"), mapOf("a" to mapOf("g" to 7), "c" to 6), 3)
        )
        assertEquals(
            mapOf("a" to mapOf("b" to 3)),
            DynamicUtil.setValueAtPath(listOf("a", "b"), mapOf("a" to mapOf("b" to 8)), 3)
        )
    }

    @Test
    fun getValueAtPath() {
        val theMap = mapOf("a" to mapOf("b" to 1, "c" to 2), "d" to 3)
        assertEquals(
            mapOf("b" to 1, "c" to 2),
            DynamicUtil.getValueAtPath(listOf("a"), theMap)
        )
        assertEquals(
            1,
            DynamicUtil.getValueAtPath(listOf("a", "b"), theMap)
        )
        assertEquals(
            2,
            DynamicUtil.getValueAtPath(listOf("a", "c"), theMap)
        )
        assertEquals(
            3,
            DynamicUtil.getValueAtPath(listOf("d"), theMap)
        )
    }

    @Test
    fun pathExists() {
        val theMap = mapOf("a" to mapOf("b" to 1, "c" to 2), "d" to 3)

        assertTrue(DynamicUtil.pathExists(listOf(), theMap))
        assertTrue(DynamicUtil.pathExists(listOf("a"), theMap))
        assertTrue(DynamicUtil.pathExists(listOf("a", "b"), theMap))
        assertTrue(DynamicUtil.pathExists(listOf("a", "c"), theMap))
        assertTrue(DynamicUtil.pathExists(listOf("d"), theMap))

        assertFalse(DynamicUtil.pathExists(listOf("a", "d"), theMap))
        assertFalse(DynamicUtil.pathExists(listOf("b"), theMap))

        assertFalse(DynamicUtil.pathExists(listOf("a"), null))
        assertFalse(DynamicUtil.pathExists(listOf("a", "b"), null))
        assertFalse(DynamicUtil.pathExists(listOf("a", "c"), null))
        assertFalse(DynamicUtil.pathExists(listOf("d"), null))

        assertTrue(DynamicUtil.pathExists(listOf(), null))
    }
}
