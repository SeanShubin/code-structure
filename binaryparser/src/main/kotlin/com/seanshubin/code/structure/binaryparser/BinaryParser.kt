package com.seanshubin.code.structure.binaryparser

import java.nio.file.Path

interface BinaryParser {
    fun parseBinary(path: Path, names: List<String>): List<BinaryDetail>
}
