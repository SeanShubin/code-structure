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
import java.time.Duration

class Dependencies(args: Array<String>) {
    private val configFileName = args.getOrNull(0) ?: "code-structure-config.json"
    private val configFile = Paths.get(configFileName)
    private val files: FilesContract = FilesDelegate
    private val config: Configuration = JsonFileConfiguration(files, configFile)
    private val clock: Clock = Clock.systemUTC()
    private val inputDir = config.load(listOf("inputDir"), ".").coerceToPath()
    private val outputDir = config.load(listOf("outputDir"), "generated").coerceToPath()
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
    private val sourcesReport: Report = SourcesReport()
    private val reports: List<Report> = listOf(sourcesReport)
    private val reportWrapper: ReportWrapper = ReportWrapperImpl()
    private val reportGenerator: ReportGenerator = ReportGeneratorImpl(reports, outputDir, reportWrapper)
    private val environment: Environment = EnvironmentImpl(files)
    private val commandRunner: CommandRunner = CommandRunnerImpl(environment)
    private val emitLine: (String) -> Unit = ::println
    private val notifications: Notifications = NotificationsImpl(emitLine)
    private val timeTakenEvent: (Duration) -> Unit = notifications::timeTakenEvent
    val runner: Runnable = Runner(
        clock, observer, analyzer, reportGenerator, commandRunner, timeTakenEvent
    )
}
