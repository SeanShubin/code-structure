package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.parser.SourceDetail
import java.nio.file.Path

data class Observations(
    val inputDir: Path,
    val sourcePrefix: String,
    val sourceFiles: List<Path>,
    val sourceDetailByPath: Map<Path, SourceDetail>
)
