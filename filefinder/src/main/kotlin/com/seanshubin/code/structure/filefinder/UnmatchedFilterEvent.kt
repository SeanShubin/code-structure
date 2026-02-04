package com.seanshubin.code.structure.filefinder

import java.nio.file.Path

data class UnmatchedFilterEvent(
    val category: String,
    val file: Path
)
