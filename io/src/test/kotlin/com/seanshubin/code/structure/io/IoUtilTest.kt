package com.seanshubin.code.structure.io

import com.seanshubin.code.structure.io.IoUtil.consumeString
import com.seanshubin.code.structure.io.IoUtil.sendTo
import com.seanshubin.code.structure.io.IoUtil.toInputStream
import com.seanshubin.code.structure.io.IoUtil.toIterator
import com.seanshubin.code.structure.io.IoUtil.toReader
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import kotlin.test.Test
import kotlin.test.assertEquals

class IoUtilTest {
    private val charset = StandardCharsets.UTF_8

    @Test
    fun testBytes() {
        val inputStream = "Hello, world!".toInputStream(charset)
        val string = inputStream.consumeString(charset)
        assertEquals("Hello, world!", string)
    }

    @Test
    fun testStringToOutputStream() {
        val original = "Hello, world!"
        val outputStream = ByteArrayOutputStream()
        original.sendTo(charset, outputStream)
        val string = String(outputStream.toByteArray(), charset)
        assertEquals("Hello, world!", string)
    }

    @Test
    fun testBytesToOutputStream() {
        val original = "Hello, world!"
        val bytes = original.toByteArray(charset)
        val outputStream = ByteArrayOutputStream()
        bytes.sendTo(outputStream)
        val string = String(outputStream.toByteArray(), charset)
        assertEquals("Hello, world!", string)
    }

    @Test
    fun testChars() {
        val reader = "Hello, world!".toReader()
        val string = reader.consumeString()
        assertEquals("Hello, world!", string)
    }

    @Test
    fun testIterator() {
        val original = "Hello, world!"
        val reader = original.toReader()
        val iterator = reader.toIterator()
        val fromIterator = iterator.asSequence().joinToString("")
        assertEquals(original, fromIterator)
    }
}
