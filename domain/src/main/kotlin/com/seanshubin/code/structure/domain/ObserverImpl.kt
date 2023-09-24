package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.parser.Parser
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class ObserverImpl(
    private val inputDir: Path,
    private val sourcePrefix: String,
    private val isSourceFile: (Path) -> Boolean,
    private val fileFinder: FileFinder,
    private val parser: Parser,
    private val files:FilesContract
) : Observer {
    override fun makeObservations(): Observations {
        val sourceFiles = fileFinder.findFiles(inputDir, isSourceFile).sorted()
        val sourceDetailByPath = sourceFiles.associateWith { path ->
            val content = files.readString(path, StandardCharsets.UTF_8)
            val sourceDetail = parser.parseSource(path, content)
            sourceDetail
        }
        return Observations(inputDir, sourcePrefix, sourceFiles, sourceDetailByPath)
    }
}
