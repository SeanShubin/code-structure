package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.beamformat.BeamParserImpl
import com.seanshubin.code.structure.bytecodeformat.BinaryParser
import com.seanshubin.code.structure.bytecodeformat.BinaryParserRepository
import com.seanshubin.code.structure.config.Configuration
import com.seanshubin.code.structure.config.JsonFileConfiguration
import com.seanshubin.code.structure.config.TypeUtil.coerceToListOfString
import com.seanshubin.code.structure.config.TypeUtil.coerceToPath
import com.seanshubin.code.structure.config.TypeUtil.coerceToString
import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.FilesDelegate
import com.seanshubin.code.structure.domain.*
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.filefinder.FileFinderImpl
import com.seanshubin.code.structure.filefinder.RegexFileMatcher
import com.seanshubin.code.structure.jvmformat.*
import com.seanshubin.code.structure.parser.*
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
    private val language = config.load(listOf("language"), "source code language").coerceToString()
    private val bytecodeFormat = config.load(listOf("bytecodeFormat"), "bytecode format").coerceToString()
    private val sourcePrefix = config.load(listOf("sourcePrefix"), "prefix for link to source code").coerceToString()
    private val sourceFileIncludeRegexPatterns: List<String> =
        config.load(listOf("sourceFileRegexPatterns", "include"), emptyList<String>()).coerceToListOfString()
    private val sourceFileExcludeRegexPatterns: List<String> =
        config.load(listOf("sourceFileRegexPatterns", "exclude"), emptyList<String>()).coerceToListOfString()
    private val isSourceFile: (Path) -> Boolean = RegexFileMatcher(
        inputDir,
        sourceFileIncludeRegexPatterns,
        sourceFileExcludeRegexPatterns
    )
    private val binaryFileIncludeRegexPatterns: List<String> =
        config.load(listOf("binaryFileRegexPatterns", "include"), emptyList<String>()).coerceToListOfString()
    private val binaryFileExcludeRegexPatterns: List<String> =
        config.load(listOf("binaryFileRegexPatterns", "exclude"), emptyList<String>()).coerceToListOfString()
    private val isBinaryFile: (Path) -> Boolean = RegexFileMatcher(
        inputDir,
        binaryFileIncludeRegexPatterns,
        binaryFileExcludeRegexPatterns
    )
    private val fileFinder: FileFinder = FileFinderImpl(files)
    private val kotlinSourceParser: KotlinSourceParser = KotlinSourceParserImpl(inputDir)
    private val elixirParser: ElixirParser = ElixirParserImpl(inputDir)
    private val sourceParserRepository: SourceParserRepository = SourceParserRepositoryImpl(
        kotlinSourceParser,
        elixirParser
    )
    private val zipByteSequenceLoader: ZipByteSequenceLoader = ZipByteSequenceLoaderImpl(
        files
    )
    private val fileByteSequenceLoader: FileByteSequenceLoader = FileByteSequenceLoaderImpl(
        files
    )
    private val byteSequenceLoader: ByteSequenceLoader = ZipOrFileByteSequenceLoader(
        zipByteSequenceLoader,
        fileByteSequenceLoader
    )
    private val classInfoLoader: ClassInfoLoaderImpl = ClassInfoLoaderImpl()
    private val classParser: ClassParser = ClassParserImpl(inputDir, byteSequenceLoader, classInfoLoader)
    private val beamParser: BeamParser = BeamParserImpl(files, inputDir)
    private val binaryParserRepository: BinaryParserRepository = BinaryParserRepositoryImpl(
        classParser,
        beamParser
    )
    private val sourceParser: SourceParser = sourceParserRepository.lookupByLanguage(language)
    private val binaryParser: BinaryParser = binaryParserRepository.lookupByBytecodeFormat(bytecodeFormat)
    private val observer: Observer = ObserverImpl(
        inputDir,
        sourcePrefix,
        isSourceFile,
        isBinaryFile,
        fileFinder,
        sourceParser,
        binaryParser,
        files
    )
    private val analyzer: Analyzer = AnalyzerImpl()
    private val staticContentReport: Report = StaticContentReport()
    private val sourcesReport: Report = SourcesReport()
    private val tableOfContentsReport: Report = TableOfContentsReport()
    private val binariesReport: Report = BinariesReport()
    private val reports: List<Report> = listOf(
        staticContentReport,
        tableOfContentsReport,
        sourcesReport,
        binariesReport
    )
    private val reportGenerator: ReportGenerator = ReportGeneratorImpl(reports, outputDir)
    private val environment: Environment = EnvironmentImpl(files)
    private val commandRunner: CommandRunner = CommandRunnerImpl(environment)
    private val emitLine: (String) -> Unit = ::println
    private val notifications: Notifications = NotificationsImpl(emitLine)
    private val timeTakenEvent: (Duration) -> Unit = notifications::timeTakenEvent
    val runner: Runnable = Runner(
        clock, observer, analyzer, reportGenerator, commandRunner, timeTakenEvent
    )
}
