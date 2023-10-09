package com.seanshubin.code.structure.sourceparser

import java.nio.file.Path

interface SourceParser {
    fun parseSource(path: Path, content: String): SourceDetail
}
