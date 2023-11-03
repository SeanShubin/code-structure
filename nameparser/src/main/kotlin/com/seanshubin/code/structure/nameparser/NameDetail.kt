package com.seanshubin.code.structure.nameparser

import java.nio.file.Path

data class NameDetail(
    val path: Path,
    val language: String,
    val modules: List<String>,
    val errorLines: List<String>
)