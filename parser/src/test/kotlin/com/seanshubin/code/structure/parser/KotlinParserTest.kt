package com.seanshubin.code.structure.parser

import org.junit.Test
import java.nio.file.Paths
import kotlin.test.assertEquals

class KotlinParserTest {
    @Test
    fun simpleClass() {
        // given
        val parser = KotlinParser()
        val path = Paths.get("foo/bar/Sample.kt")
        val content = """
            package foo.bar

            class Sample(val x:Int)
        """.trimIndent()
        val expected = SourceDetail(
            path,
            "kotlin",
            listOf("foo.bar.Sample"),
            emptyList()
        )

        // when
        val actual = parser.parseSource(path, content)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun abstractClass() {
        // given
        val parser = KotlinParser()
        val path = Paths.get("foo/bar/Sample.kt")
        val content = """
            package foo.bar

            abstract class Sample(val x:Int)
        """.trimIndent()
        val expected = SourceDetail(
            path,
            "kotlin",
            listOf("foo.bar.Sample"),
            emptyList()
        )

        // when
        val actual = parser.parseSource(path, content)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun simpleInterface() {
        // given
        val parser = KotlinParser()
        val path = Paths.get("foo/bar/Sample.kt")
        val content = """
            package foo.bar

            interface Sample {
            }
        """.trimIndent()
        val expected = SourceDetail(
            path,
            "kotlin",
            listOf("foo.bar.Sample"),
            emptyList()
        )

        // when
        val actual = parser.parseSource(path, content)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun rootPackage() {
        // given
        val parser = KotlinParser()
        val path = Paths.get("Sample.kt")
        val content = """
            class Sample(val x:Int)
        """.trimIndent()
        val expected = SourceDetail(
            path,
            "kotlin",
            listOf("Sample"),
            emptyList()
        )

        // when
        val actual = parser.parseSource(path, content)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun dataClass() {
        // given
        val parser = KotlinParser()
        val path = Paths.get("foo/bar/Sample.kt")
        val content = """
            package foo.bar

            data class Sample(val x:Int)
        """.trimIndent()
        val expected = SourceDetail(
            path,
            "kotlin",
            listOf("foo.bar.Sample"),
            emptyList()
        )

        // when
        val actual = parser.parseSource(path, content)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun objectModule() {
        // given
        val parser = KotlinParser()
        val path = Paths.get("foo/bar/Sample.kt")
        val content = """
            package foo.bar

            object Sample {}
        """.trimIndent()
        val expected = SourceDetail(
            path,
            "kotlin",
            listOf("foo.bar.Sample"),
            emptyList()
        )

        // when
        val actual = parser.parseSource(path, content)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun multiple() {
        // given
        val parser = KotlinParser()
        val path = Paths.get("foo/bar/Sample.kt")
        val content = """
            package foo.bar
            
            class Sample {
                class InnerClass
                companion object {
                    class InnerObject
                }
            }
            
            class SampleTwo
            
            object SampleThree
        """.trimIndent()
        val expected = SourceDetail(
            path,
            "kotlin",
            listOf(
                "foo.bar.Sample",
                "foo.bar.SampleTwo",
                "foo.bar.SampleThree"
            ),
            emptyList()
        )

        // when
        val actual = parser.parseSource(path, content)

        // then
        assertEquals(expected, actual)
    }
}
