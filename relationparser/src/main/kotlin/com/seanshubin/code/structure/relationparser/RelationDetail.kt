package com.seanshubin.code.structure.relationparser

import java.nio.file.Path

data class RelationDetail(
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
