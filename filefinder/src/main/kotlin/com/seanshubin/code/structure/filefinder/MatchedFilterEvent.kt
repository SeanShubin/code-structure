package com.seanshubin.code.structure.filefinder

import java.nio.file.Path

data class MatchedFilterEvent(
    val category: String,
    val type: String,
    val pattern: String,
    val file: Path
)
