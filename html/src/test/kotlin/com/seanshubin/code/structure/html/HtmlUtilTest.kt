package com.seanshubin.code.structure.html

import kotlin.test.Test
import kotlin.test.assertEquals

//
// This file was imported from: ../kotlin-reusable
// Module: html
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

class HtmlUtilTest {
    @Test
    fun anchor() {
        val element = HtmlUtil.anchor("Click here", "https://example.com")
        val expected = listOf(
            """<a href="https://example.com">""",
            "  Click here",
            "</a>"
        )
        assertEquals(expected, element.toLines())
    }

    @Test
    fun createTable() {
        data class Person(val name: String, val age: Int)
        val people = listOf(
            Person("Alice", 30),
            Person("Bob", 25)
        )
        val elements = HtmlUtil.createTableWithText(
            list = people,
            captions = listOf("Name", "Age"),
            elementToRow = { person -> listOf(person.name, person.age.toString()) },
            caption = "People"
        )
        val expected = listOf(
            "<p>",
            "  People count: 2",
            "</p>",
            "<table>",
            "  <thead>",
            "    <tr>",
            "      <th>",
            "        Name",
            "      </th>",
            "      <th>",
            "        Age",
            "      </th>",
            "    </tr>",
            "  </thead>",
            "  <tbody>",
            "    <tr>",
            "      <td>",
            "        Alice",
            "      </td>",
            "      <td>",
            "        30",
            "      </td>",
            "    </tr>",
            "    <tr>",
            "      <td>",
            "        Bob",
            "      </td>",
            "      <td>",
            "        25",
            "      </td>",
            "    </tr>",
            "  </tbody>",
            "</table>"
        )
        assertEquals(expected, elements.flatMap { it.toLines() })
    }

    @Test
    fun createTableWithoutCaption() {
        data class Item(val id: Int)
        val items = listOf(Item(1), Item(2))
        val elements = HtmlUtil.createTableWithText(
            list = items,
            captions = listOf("ID"),
            elementToRow = { item -> listOf(item.id.toString()) }
        )
        val expected = listOf(
            "<table>",
            "  <thead>",
            "    <tr>",
            "      <th>",
            "        ID",
            "      </th>",
            "    </tr>",
            "  </thead>",
            "  <tbody>",
            "    <tr>",
            "      <td>",
            "        1",
            "      </td>",
            "    </tr>",
            "    <tr>",
            "      <td>",
            "        2",
            "      </td>",
            "    </tr>",
            "  </tbody>",
            "</table>"
        )
        assertEquals(expected, elements.flatMap { it.toLines() })
    }

    @Test
    fun listItems() {
        val items = listOf("Apple", "Banana", "Cherry")
        val element = HtmlUtil.listItems(items)
        val expected = listOf(
            "<ul>",
            "  <li>",
            "    Apple",
            "  </li>",
            "  <li>",
            "    Banana",
            "  </li>",
            "  <li>",
            "    Cherry",
            "  </li>",
            "</ul>"
        )
        assertEquals(expected, element.toLines())
    }

    @Test
    fun orderedListItems() {
        val items = listOf("First", "Second", "Third")
        val element = HtmlUtil.orderedListItems(items)
        val expected = listOf(
            "<ol>",
            "  <li>",
            "    First",
            "  </li>",
            "  <li>",
            "    Second",
            "  </li>",
            "  <li>",
            "    Third",
            "  </li>",
            "</ol>"
        )
        assertEquals(expected, element.toLines())
    }

    @Test
    fun emptyList() {
        val element = HtmlUtil.listItems(emptyList<String>())
        val expected = listOf("<ul>", "</ul>")
        assertEquals(expected, element.toLines())
    }
}
