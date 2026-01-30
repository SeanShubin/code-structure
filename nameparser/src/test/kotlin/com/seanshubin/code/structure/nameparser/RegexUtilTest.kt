package com.seanshubin.code.structure.nameparser

import kotlin.test.Test
import kotlin.test.assertEquals

class RegexUtilTest {

    @Test
    fun fileName() {
        val fileNameRegex = Regex("""(\w+)\.kt$""")
        assertEquals("Foo", RegexUtil.findByRegex(fileNameRegex, "a/b/c/Foo.kt"))
        assertEquals("Foo", RegexUtil.findByRegex(fileNameRegex, "Foo.kt"))
    }
}