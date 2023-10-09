package com.seanshubin.code.structure.jvmformat

interface ClassInfoLoader {
    fun fromBytes(bytes: List<Byte>): JvmClass
}