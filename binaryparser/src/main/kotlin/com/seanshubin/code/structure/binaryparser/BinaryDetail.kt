package com.seanshubin.code.structure.binaryparser

import java.nio.file.Path

data class BinaryDetail(
    val file: Path,
    val pathInFile: String,
    val name: String,
    val dependencyNames: List<String>
) {
    val location: String = if (pathInFile == "") {
        file.toString()
    } else {
        "$file!$pathInFile"
    }
}
