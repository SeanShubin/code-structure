package com.seanshubin.code.structure.untyped

import java.nio.file.Path
import java.nio.file.Paths

data class Untyped(val value: Any?) {
    fun asPath(): Path =
        when (value) {
            is Path -> value
            is String -> Paths.get(value)
            else -> throw RuntimeException("Unsupported conversion from ${typeName(value)} to Path")
        }

    fun asListOfString(): List<String> {
        throw UnsupportedOperationException("not implemented")
    }

    fun asString(): String = value as String

    private fun typeName(value: Any?): String =
        when (value) {
            null -> "<null>"
            else -> value.javaClass.typeName
        }
}
