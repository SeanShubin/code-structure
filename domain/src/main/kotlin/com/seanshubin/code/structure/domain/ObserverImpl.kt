package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.filefinder.FileFinder
import java.nio.file.Path

class ObserverImpl(
    private val inputDir: Path,
    private val isSourceFile: (Path) -> Boolean,
    private val fileFinder: FileFinder,
) : Observer {
    override fun makeObservations(): Observations {
        val sourceFiles = fileFinder.findFiles(inputDir, isSourceFile)
        return Observations(sourceFiles)
    }
}
