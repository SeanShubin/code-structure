package com.seanshubin.code.structure.domain

import com.fasterxml.jackson.module.kotlin.readValue
import com.seanshubin.code.structure.binaryparser.BinaryParser
import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.domain.ErrorsDto.Companion.jsonToErrors
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.json.JsonMappers
import com.seanshubin.code.structure.sourceparser.SourceParser
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class ObserverImpl(
    private val inputDir: Path,
    private val configuredErrorsPath: Path,
    private val sourcePrefix: String,
    private val isSourceFile: (Path) -> Boolean,
    private val isBinaryFile: (Path) -> Boolean,
    private val fileFinder: FileFinder,
    private val sourceParser: SourceParser,
    private val binaryParser: BinaryParser,
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
            val sourceDetail = sourceParser.parseSource(path, content)
            sourceDetail
        }
        val names = sourceDetailList.flatMap { it.modules }.distinct().sorted()
        val binaryFiles = fileFinder.findFiles(inputDir, isBinaryFile).sorted()
        val binaryDetailList = binaryFiles.flatMap { binaryParser.parseBinary(it, names) }
        return Observations(inputDir, sourcePrefix, sourceFiles, sourceDetailList, binaryDetailList, configuredErrors)
    }
}
