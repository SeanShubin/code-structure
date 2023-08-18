package com.seanshubin.code.structure.config

import com.seanshubin.code.structure.config.TypeUtil.coerceToListOfString
import com.seanshubin.code.structure.config.TypeUtil.coerceToPath
import com.seanshubin.code.structure.config.TypeUtil.coerceToString
import org.junit.Assert.assertThrows
import org.junit.Test
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals

class TypeUtilTest {
    @Test
    fun path(){
        val path:Any? = Paths.get("path/file1.txt")
        val string:Any? = "path/file1.txt"
        val expected: Path = Paths.get("path/file1.txt")
        assertEquals(expected, string.coerceToPath())
        assertEquals(expected, path.coerceToPath())

        val exception = assertThrows(RuntimeException::class.java){
            1.coerceToPath()
        }
        assertEquals("Unable to convert 1 of type java.lang.Integer to java.nio.file.Path", exception.message)
    }

    @Test
    fun string(){
        val string:Any? = "the-string"
        val expected: String = "the-string"
        assertEquals(expected, string.coerceToString())
    }

    @Test
    fun listOfString(){
        val list:Any? = listOf("a", "b", "c")
        val expected: List<String> = listOf("a", "b", "c")
        assertEquals(expected, list.coerceToListOfString())
    }
}