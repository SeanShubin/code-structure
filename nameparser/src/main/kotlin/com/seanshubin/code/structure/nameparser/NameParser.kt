package com.seanshubin.code.structure.nameparser

import java.nio.file.Path

interface NameParser {
    fun parseName(path: Path, content: String): NameDetail
}