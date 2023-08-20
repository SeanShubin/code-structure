package com.seanshubin.code.structure.domain

import java.nio.file.Path

data class Observations(
    val inputDir: Path,
    val sourcePrefix: String,
    val sourceFiles: List<Path>
)
