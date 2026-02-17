package com.seanshubin.code.structure.html

import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import kotlin.test.Test
import kotlin.test.assertEquals

//
// This file was imported from: ../kotlin-reusable
// Module: html
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

class HtmlElementTest {
    @Test
    fun simpleTag() {
        val element = Tag("div")
        val expected = listOf("<div>", "</div>")
        assertEquals(expected, element.toLines())
    }

    @Test
    fun simpleText() {
        val element = Text("Hello, World!")
        val expected = listOf("Hello, World!")
        assertEquals(expected, element.toLines())
    }

    @Test
    fun tagWithText() {
        val element = Tag("p", Text("paragraph content"))
        val expected = listOf(
            "<p>",
            "  paragraph content",
            "</p>"
        )
        assertEquals(expected, element.toLines())
    }

    @Test
    fun tagWithAttributes() {
        val element = Tag(
            name = "a",
            children = listOf(Text("Click here")),
            attributes = listOf("href" to "https://example.com", "target" to "_blank")
        )
        val expected = listOf(
            """<a href="https://example.com" target="_blank">""",
            "  Click here",
            "</a>"
        )
        assertEquals(expected, element.toLines())
    }

    @Test
    fun nestedTags() {
        val element = Tag(
            "div",
            listOf(
                Tag("h1", Text("Title")),
                Tag("p", Text("Paragraph"))
            )
        )
        val expected = listOf(
            "<div>",
            "  <h1>",
            "    Title",
            "  </h1>",
            "  <p>",
            "    Paragraph",
            "  </p>",
            "</div>"
        )
        assertEquals(expected, element.toLines())
    }

    @Test
    fun varargConstructor() {
        val element = Tag(
            "ul",
            Tag("li", Text("Item 1")),
            Tag("li", Text("Item 2")),
            Tag("li", Text("Item 3"))
        )
        val expected = listOf(
            "<ul>",
            "  <li>",
            "    Item 1",
            "  </li>",
            "  <li>",
            "    Item 2",
            "  </li>",
            "  <li>",
            "    Item 3",
            "  </li>",
            "</ul>"
        )
        assertEquals(expected, element.toLines())
    }

    @Test
    fun multilineText() {
        val element = Tag(
            "pre",
            Text(listOf("Line 1", "Line 2", "Line 3"))
        )
        val expected = listOf(
            "<pre>",
            "  Line 1",
            "  Line 2",
            "  Line 3",
            "</pre>"
        )
        assertEquals(expected, element.toLines())
    }

    @Test
    fun companionTextFunction() {
        val element = HtmlElement.tagText("h1", "Hello")
        val expected = listOf(
            "<h1>",
            "  Hello",
            "</h1>"
        )
        assertEquals(expected, element.toLines())
    }

    @Test
    fun complexDocument() {
        val element = Tag(
            "html",
            listOf(
                Tag(
                    "head",
                    listOf(
                        Tag("title", Text("My Page"))
                    )
                ),
                Tag(
                    "body",
                    listOf(
                        Tag("h1", Text("Welcome")),
                        Tag(
                            "div",
                            listOf(
                                Tag("p", Text("First paragraph")),
                                Tag("p", Text("Second paragraph"))
                            ),
                            listOf("class" to "content")
                        )
                    )
                )
            )
        )
        val expected = listOf(
            "<html>",
            "  <head>",
            "    <title>",
            "      My Page",
            "    </title>",
            "  </head>",
            "  <body>",
            "    <h1>",
            "      Welcome",
            "    </h1>",
            """    <div class="content">""",
            "      <p>",
            "        First paragraph",
            "      </p>",
            "      <p>",
            "        Second paragraph",
            "      </p>",
            "    </div>",
            "  </body>",
            "</html>"
        )
        assertEquals(expected, element.toLines())
    }

    @Test
    fun emptyTag() {
        val element = Tag("div", emptyList())
        val expected = listOf("<div>", "</div>")
        assertEquals(expected, element.toLines())
    }
}
