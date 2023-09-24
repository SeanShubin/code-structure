package com.seanshubin.code.structure.parser

import java.nio.file.Path

interface Parser {
    fun parseSource(path: Path, content: String): SourceDetail
}
