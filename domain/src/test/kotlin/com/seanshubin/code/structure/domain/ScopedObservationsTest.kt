package com.seanshubin.code.structure.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class ScopedObservationsTest {
    val references = listOf(
        "a.c" to "a.c.g",
        "a" to "a.c",
        "a.c.g" to "a.c.h",
        "a.c.g" to "a.d",
        "a.c.h" to "a.d.j",
        "a.d.i" to "a",
        "a.d" to "a.c",
        "a.c" to "b",
        "a.c" to "b.e.k",
        "a" to "b.e.l",
        "a" to "b",
        "a.d.j" to "b.f.m",
        "b" to "b.f.m",
        "b.e.l" to "b.e",
        "b.e.l" to "b.f.n",
        "b.e" to "b"
    )

    @Test
    fun createAll() {
        val expected = listOf(
            ScopedObservations(
                groupPath = emptyList(),
                referenceReasons = mapOf(
                    "a" to "b" to listOf(
                        "a.c" to "b",
                        "a.c" to "b.e.k",
                        "a" to "b.e.l",
                        "a" to "b",
                        "a.d.j" to "b.f.m"
                    )
                )
            ),
            ScopedObservations(
                groupPath = listOf("a"),
                referenceReasons = mapOf(
                    "a.c" to "a.d" to listOf(
                        "a.c.g" to "a.d",
                        "a.c.h" to "a.d.j"
                    ),
                    "a.d" to "a.c" to listOf(
                        "a.d" to "a.c"
                    )
                )
            ),
            ScopedObservations(
                groupPath = listOf("a", "c"),
                referenceReasons = mapOf(
                    "a.c.g" to "a.c.h" to listOf(
                        "a.c.g" to "a.c.h"
                    )
                )
            ),
            ScopedObservations(
                groupPath = listOf("b"),
                referenceReasons = mapOf(
                    "b.e" to "b.f" to listOf(
                        "b.e.l" to "b.f.n"
                    )
                )
            ),
            ScopedObservations(
                groupPath = listOf("b", "e"),
                referenceReasons = emptyMap()
            )
        )
        val actual = ScopedObservations.create(references)
        assertEquals(expected, actual)
    }
}
