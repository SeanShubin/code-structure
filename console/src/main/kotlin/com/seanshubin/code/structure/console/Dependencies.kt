package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.config.Configuration
import com.seanshubin.code.structure.config.JsonFileConfiguration
import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.FilesDelegate
import com.seanshubin.code.structure.domain.ReportGenerator
import com.seanshubin.code.structure.domain.ReportGeneratorImpl
import com.seanshubin.code.structure.domain.Runner
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.filefinder.FileFinderImpl
import com.seanshubin.code.structure.filefinder.RegexFileMatcher
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Clock

class Dependencies(args: Array<String>) {
    private val configFileName = args.getOrNull(0) ?: "code-structure-config.json"
    private val configFile = Paths.get(configFileName)
    private val config: Configuration = JsonFileConfiguration(configFile)
    private val clock: Clock = Clock.systemUTC()
    private val inputDir = config.load(".", listOf("inputDir")).toPath()
    private val sourceFileIncludeRegexPatterns:List<String> = config.load(emptyList<String>(),listOf("sourceFileRegexPatterns", "include")).toListOfString()
    private val sourceFileExcludeRegexPatterns:List<String> = config.load(emptyList<String>(),listOf("sourceFileRegexPatterns", "exclude")).toListOfString()
    private val isSourceFile:(Path)->Boolean = RegexFileMatcher(
        sourceFileIncludeRegexPatterns,
        sourceFileExcludeRegexPatterns)
    private val files: FilesContract = FilesDelegate
    private val fileFinder: FileFinder = FileFinderImpl(files)
    private val reportGenerator:ReportGenerator = ReportGeneratorImpl()

    val runner: Runnable = Runner(
        clock, inputDir, isSourceFile, fileFinder, reportGenerator
    )
}
