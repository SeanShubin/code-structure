package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.relationparser.RelationParser
import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.domain.ErrorsDto.Companion.jsonToErrors
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.nameparser.NameParser
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class ObserverImpl(
    private val inputDir: Path,
    private val configuredErrorsPath: Path,
    private val sourcePrefix: String,
    private val isSourceFile: (Path) -> Boolean,
    private val isBinaryFile: (Path) -> Boolean,
    private val fileFinder: FileFinder,
    private val nameParser: NameParser,
    private val relationParser: RelationParser,
    private val files: FilesContract
) : Observer {
    override fun makeObservations(): Observations {
        val configuredErrors = if (files.exists(configuredErrorsPath)) {
            val configuredErrorsText = files.readString(configuredErrorsPath, StandardCharsets.UTF_8)
            configuredErrorsText.jsonToErrors()
        } else {
            null
        }
        val sourceFiles = fileFinder.findFiles(inputDir, isSourceFile).sorted()
        val sourceDetailList = sourceFiles.map { path ->
            val content = files.readString(path, StandardCharsets.UTF_8)
            val sourceDetail = nameParser.parseName(path, content)
            sourceDetail
        }
        val names = sourceDetailList.flatMap { it.modules }.distinct().sorted()
        val binaryFiles = fileFinder.findFiles(inputDir, isBinaryFile).sorted()
        val binaryDetailList = binaryFiles.flatMap { relationParser.parseDependencies(it, names) }
        return Observations(inputDir, sourcePrefix, sourceFiles, sourceDetailList, binaryDetailList, configuredErrors)
    }
}
