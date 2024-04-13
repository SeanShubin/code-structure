package com.seanshubin.code.structure.domain

import com.fasterxml.jackson.module.kotlin.readValue
import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.domain.ErrorsDto.Companion.jsonToErrors
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.json.JsonMappers
import com.seanshubin.code.structure.nameparser.NameDetail
import com.seanshubin.code.structure.nameparser.NameParser
import com.seanshubin.code.structure.relationparser.RelationDetail
import com.seanshubin.code.structure.relationparser.RelationParser
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
    private val files: FilesContract,
    private val outputDir: Path,
    private val useObservationsCache: Boolean
) : Observer {
    private val observationsFile = outputDir.resolve("observations.json")
    override fun makeObservations(): Observations {
        return loadObservationsFromCache() ?: makeObservationsFromDisk()
    }

    private fun loadObservationsFromCache(): Observations? {
        if (!useObservationsCache) return null
        if (!files.exists(observationsFile)) return null
        val text = files.readString(observationsFile, StandardCharsets.UTF_8)
        val observations = JsonMappers.parser.readValue<Observations>(text)
        return observations
    }

    private fun makeObservationsFromDisk(): Observations {
        val configuredErrors = if (files.exists(configuredErrorsPath)) {
            val configuredErrorsText = files.readString(configuredErrorsPath, StandardCharsets.UTF_8)
            configuredErrorsText.jsonToErrors()
        } else {
            null
        }
        val absoluteSourceFiles = fileFinder.findFiles(inputDir, isSourceFile).sorted()
        val relativeSourceFiles = absoluteSourceFiles.map { inputDir.relativize(it) }
        val nameDetailList = absoluteSourceFiles.map { path ->
            val content = files.readString(path, StandardCharsets.UTF_8)
            val nameDetail = nameParser.parseName(path, content)
            nameDetail
        }
        val names = nameDetailList.flatMap { it.modules }.distinct().sorted()
        val binaryFiles = fileFinder.findFiles(inputDir, isBinaryFile).sorted()
        val binaryDetailList = binaryFiles.flatMap {
            relationParser.parseDependencies(it, names)
        }.sorted()
        val binaryDetailNames = binaryDetailList.map { it.name }
        val (namesInBinary, namesNotInBinary) = names.partition { name ->
            binaryDetailNames.contains(name)
        }
        val missingBinaries = nameDetailList.filter { nameDetail ->
            nameDetail.modules.any { module ->
                namesNotInBinary.contains(module)
            }
        }

        fun filterNameDetail(nameDetail: NameDetail): NameDetail? {
            val modules = nameDetail.modules.filter { namesInBinary.contains(it) }
            if (modules.isEmpty()) return null
            return nameDetail.copy(modules = modules)
        }

        val filteredNameDetailList = nameDetailList.mapNotNull(::filterNameDetail)
        fun filterBinaryDetail(relationDetail: RelationDetail): RelationDetail {
            val dependencyNames = relationDetail.dependencyNames.filter { namesInBinary.contains(it) }
            return relationDetail.copy(dependencyNames = dependencyNames)
        }

        val filteredReferenceDetailList = binaryDetailList.map(::filterBinaryDetail)
        val observations = Observations(
            inputDir,
            sourcePrefix,
            relativeSourceFiles,
            filteredNameDetailList,
            filteredReferenceDetailList,
            missingBinaries,
            configuredErrors
        )
        val text = JsonMappers.pretty.writeValueAsString(observations)
        files.createDirectories(outputDir)
        files.writeString(observationsFile, text, StandardCharsets.UTF_8)
        return observations
    }
}
