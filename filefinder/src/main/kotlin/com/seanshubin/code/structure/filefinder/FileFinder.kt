package com.seanshubin.code.structure.filefinder

import java.nio.file.Path

interface FileFinder {
    fun findFiles(baseDir: Path, accept: (Path) -> Boolean): List<Path>
}
