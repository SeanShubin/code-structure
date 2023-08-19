package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.config.Configuration
import com.seanshubin.code.structure.config.JsonFileConfiguration
import com.seanshubin.code.structure.config.TypeUtil.coerceToListOfString
import com.seanshubin.code.structure.config.TypeUtil.coerceToPath
import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.FilesDelegate
import com.seanshubin.code.structure.domain.*
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.filefinder.FileFinderImpl
import com.seanshubin.code.structure.filefinder.RegexFileMatcher
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Clock

class Dependencies(args: Array<String>) {
    private val configFileName = args.getOrNull(0) ?: "code-structure-config.json"
    private val configFile = Paths.get(configFileName)
    private val files: FilesContract = FilesDelegate
    private val config: Configuration = JsonFileConfiguration(files, configFile)
    private val clock: Clock = Clock.systemUTC()
    private val inputDir = config.load(listOf("inputDir"), ".").coerceToPath()
    private val sourceFileIncludeRegexPatterns: List<String> =
        config.load(listOf("sourceFileRegexPatterns", "include"), emptyList<String>()).coerceToListOfString()
    private val sourceFileExcludeRegexPatterns: List<String> =
        config.load(listOf("sourceFileRegexPatterns", "exclude"), emptyList<String>()).coerceToListOfString()
    private val isSourceFile: (Path) -> Boolean = RegexFileMatcher(
        sourceFileIncludeRegexPatterns,
        sourceFileExcludeRegexPatterns
    )
    private val fileFinder: FileFinder = FileFinderImpl(files)
    private val observer: Observer = ObserverImpl(inputDir, isSourceFile, fileFinder)
    private val analyzer: Analyzer = AnalyzerImpl()
    private val reportGenerator: ReportGenerator = ReportGeneratorImpl()
    val runner: Runnable = Runner(
        clock, observer, analyzer, reportGenerator
    )
}
