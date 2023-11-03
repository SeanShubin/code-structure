package com.seanshubin.code.structure.nameparser

import java.nio.file.Path

interface SourceParser {
    fun parseSource(path: Path, content: String): SourceDetail
}