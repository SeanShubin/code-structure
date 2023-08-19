package com.seanshubin.code.structure.tree

import kotlin.test.Test
import kotlin.test.assertEquals

class TreeTest {
    @Test
    fun addValue() {
        val expected = Tree.Branch(mapOf("a" to Tree.Branch(mapOf("b" to Tree.Leaf("c")))))
        val actual = Tree.empty<String, String>().setValue(listOf("a", "b"), "c")
        assertEquals(expected, actual)
    }

    @Test
    fun getValue() {
        val expected = "c"
        val actual = Tree.empty<String, String>().setValue(listOf("a", "b"), "c").getValue(listOf("a", "b"))
        assertEquals(expected, actual)
    }

    @Test
    fun toLines() {
        val tree = Tree.empty<String, Int>()
            .setValue(listOf("a", "b", "c"), 1)
            .setValue(listOf("a", "b", "d"), 2)
            .setValue(listOf("a", "e"), 3)
            .setValue(listOf("f", "g"), 4)
        val keyOrder = Comparator<String> { o1, o2 -> o1.compareTo(o2) }
        val keyToString = { key: String -> key }
        val valueToString = { value: Int -> value.toString() }
        val actual = tree.toLines(keyOrder, keyToString, valueToString)
        val expected = """
            a
              b
                c
                  1
                d
                  2
              e
                3
            f
              g
                4
        """.trimIndent().split("\n")
        assertEquals(expected, actual)
    }

    @Test
    fun pathValues() {
        val tree = Tree.empty<String, Int>()
            .setValue(listOf("a", "b", "c"), 1)
            .setValue(listOf("a", "b", "d"), 2)
            .setValue(listOf("a", "e"), 3)
            .setValue(listOf("f", "g"), 4)
        val expected = listOf(
            listOf("a", "b", "c") to 1,
            listOf("a", "b", "d") to 2,
            listOf("a", "e") to 3,
            listOf("f", "g") to 4
        )
        val actual = tree.pathValues(emptyList())
        assertEquals(expected, actual)
    }
}
