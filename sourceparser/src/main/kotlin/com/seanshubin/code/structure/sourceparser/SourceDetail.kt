package com.seanshubin.code.structure.sourceparser

import java.nio.file.Path

data class SourceDetail(
    val path: Path,
    val language: String,
    val modules: List<String>,
    val errorLines: List<String>
)
