package com.seanshubin.code.structure.filefinder

import com.seanshubin.code.structure.contract.delegate.FilesContract
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class FileFinderImpl(private val files: FilesContract) : FileFinder {
    override fun findFiles(baseDir: Path, accept: (Path) -> Boolean): List<Path> {
        val matcher = BiPredicateAdapterUseFirst<Path, BasicFileAttributes>(accept)
        val list = files.find(baseDir, Int.MAX_VALUE, matcher).toList()
        return list
    }
}
