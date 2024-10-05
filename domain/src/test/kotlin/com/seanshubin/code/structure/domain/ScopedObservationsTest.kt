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
        val names = collectNames(references)
        val expected = listOf(
            ScopedObservations(
                groupPath = emptyList(),
                names = listOf(
                    "a",
                    "a.c",
                    "a.c.g",
                    "a.c.h",
                    "a.d",
                    "a.d.i",
                    "a.d.j",
                    "b",
                    "b.e",
                    "b.e.k",
                    "b.e.l",
                    "b.f.m",
                    "b.f.n"
                ),
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
                names = listOf("a", "a.c", "a.c.g", "a.c.h", "a.d", "a.d.i", "a.d.j"),
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
                names = listOf("a.c", "a.c.g", "a.c.h"),
                referenceReasons = mapOf(
                    "a.c.g" to "a.c.h" to listOf(
                        "a.c.g" to "a.c.h"
                    )
                )
            ),
            ScopedObservations(
                groupPath = listOf("a", "d"),
                names = listOf("a.d", "a.d.i", "a.d.j"),
                referenceReasons = emptyMap()
            ),
            ScopedObservations(
                groupPath = listOf("b"),
                names = listOf("b", "b.e", "b.e.k", "b.e.l", "b.f.m", "b.f.n"),
                referenceReasons = mapOf(
                    "b.e" to "b.f" to listOf(
                        "b.e.l" to "b.f.n"
                    )
                )
            ),
            ScopedObservations(
                groupPath = listOf("b", "e"),
                names = listOf("b.e", "b.e.k", "b.e.l"),
                referenceReasons = emptyMap()
            ),
            ScopedObservations(
                groupPath = listOf("b", "f"),
                names = listOf("b.f.m", "b.f.n"),
                referenceReasons = emptyMap()
            )
        )
        val actual = ScopedObservations.create(names, references)
        assertEquals(expected, actual)
    }

    private fun collectNames(references: List<Pair<String, String>>): List<String> {
        val names = references.flatMap { it.toList() }
        val distinctNames = names.distinct()
        return distinctNames.sorted()
    }
}
