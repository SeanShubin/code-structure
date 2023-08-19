package com.seanshubin.code.structure.config

import com.seanshubin.code.structure.contract.test.FilesNotImplemented
import java.nio.charset.Charset
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.FileAttribute
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonFileConfigurationTest {
    @Test
    fun loadString() {
        // given
        val text = """
            {
              "a" : {
                "b" : {
                  "c" : "the-string"
                }
              }
            }
        """.trimIndent()
        val path = Paths.get("config/config.json")
        val expected = "the-string"
        val files = FilesStub(mapOf("config/config.json" to text))
        val jsonFileConfiguration = JsonFileConfiguration(files, path)

        // when
        val actual = jsonFileConfiguration.load(listOf("a", "b", "c"), "the-default")

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun loadListOfString() {
        // given
        val text = """
            {
              "a" : {
                "b" : {
                  "c" : ["list", "of", "string"]
                }
              }
            }
        """.trimIndent()
        val path = Paths.get("config/config.json")
        val expected = listOf("list", "of", "string")
        val files = FilesStub(mapOf("config/config.json" to text))
        val jsonFileConfiguration = JsonFileConfiguration(files, path)

        // when
        val actual = jsonFileConfiguration.load(listOf("a", "b", "c"), "the-default")

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun missingFile() {
        // given
        val text = """
            {
              "a" : {
                "b" : {
                  "c" : "the-string"
                }
              }
            }
        """.trimIndent()
        val path = Paths.get("config/config.json")
        val expected = "the-default"
        val files = FilesStub(mapOf())
        val jsonFileConfiguration = JsonFileConfiguration(files, path)

        // when
        val actual = jsonFileConfiguration.load(listOf("a", "b", "c"), "the-default")

        // then
        assertEquals(expected, actual)
    }

    class FilesStub(val initialFileContents: Map<String, String>) : FilesNotImplemented() {
        val fileContents = mutableMapOf<String, String>()
        val createdDirectories = mutableListOf<String>()

        init {
            initialFileContents.map { (key, value) ->
                fileContents[key] = value
            }
        }

        override fun readString(path: Path, cs: Charset): String {
            val text = fileContents[path.toString()] ?: throw RuntimeException("no content defined for path $path")
            return text
        }

        override fun exists(path: Path, vararg options: LinkOption): Boolean {
            return fileContents.containsKey(path.toString())
        }

        override fun createDirectories(dir: Path, vararg attrs: FileAttribute<*>): Path {
            createdDirectories.add(dir.toString())
            return dir
        }

        override fun writeString(path: Path, csq: CharSequence, cs: Charset, vararg options: OpenOption): Path {
            fileContents[path.toString()] = csq.toString()
            return path
        }
    }
}