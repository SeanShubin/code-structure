package com.seanshubin.code.structure.untyped

import java.nio.file.Path

data class Untyped(val value: Any?) {
    fun toPath(): Path {
        throw UnsupportedOperationException("not implemented")
    }

    fun toListOfString(): List<String> {
        throw UnsupportedOperationException("not implemented")
    }
}
