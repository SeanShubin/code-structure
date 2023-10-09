package com.seanshubin.code.structure.jvmformat

import java.util.zip.ZipEntry

data class ZipContents(
    val path: List<String>,
    val zipEntry: ZipEntry,
    val bytes: List<Byte>
)
