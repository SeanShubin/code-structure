package com.seanshubin.code.structure.relationparser

import java.nio.file.Path

sealed interface SourceLocation {
    data class StandaloneFile(val file: Path) : SourceLocation
    data class ZipEntry(val zipFile: Path, val entryPath: String) : SourceLocation
}
