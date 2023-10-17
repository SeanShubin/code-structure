package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.beamformat.BeamParserImpl
import com.seanshubin.code.structure.binaryparser.BinaryParser
import com.seanshubin.code.structure.binaryparser.BinaryParserRepository
import com.seanshubin.code.structure.config.Configuration
import com.seanshubin.code.structure.config.JsonFileConfiguration
import com.seanshubin.code.structure.config.TypeUtil.coerceToInt
import com.seanshubin.code.structure.config.TypeUtil.coerceToListOfString
import com.seanshubin.code.structure.config.TypeUtil.coerceToPath
import com.seanshubin.code.structure.config.TypeUtil.coerceToString
import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.contract.FilesDelegate
import com.seanshubin.code.structure.domain.*
import com.seanshubin.code.structure.elixirsyntax.ElixirParser
import com.seanshubin.code.structure.elixirsyntax.ElixirParserImpl
import com.seanshubin.code.structure.exec.Exec
import com.seanshubin.code.structure.exec.ExecImpl
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.filefinder.FileFinderImpl
import com.seanshubin.code.structure.filefinder.RegexFileMatcher
import com.seanshubin.code.structure.jvmformat.*
import com.seanshubin.code.structure.kotlinsyntax.KotlinSourceParser
import com.seanshubin.code.structure.kotlinsyntax.KotlinSourceParserImpl
import com.seanshubin.code.structure.sourceparser.SourceParser
import com.seanshubin.code.structure.sourceparser.SourceParserRepository
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Clock
import java.time.Duration

class Dependencies(args: Array<String>) {
    private val configBaseName = if (args.isEmpty() || args[0].isBlank()) {
        "code-structure"
    } else {
        args[0]
    }
    private val configFile = Paths.get("$configBaseName-config.json")
    private val existingCycleFile = Paths.get("$configBaseName-existing-cycles.txt")
    private val files: FilesContract = FilesDelegate
    private val config: Configuration = JsonFileConfiguration(files, configFile)
    private val clock: Clock = Clock.systemUTC()
    private val inputDir = config.load(listOf("inputDir"), ".").coerceToPath()
    private val outputDir = config.load(listOf("outputDir"), "generated").coerceToPath()
    private val language = config.load(listOf("language"), "source code language").coerceToString()
    private val localDepth = config.load(listOf("localDepth"), 2).coerceToInt()
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
        existingCycleFile,
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
    private val graphReport: Report = GraphReport()
    private val cycleReport: Report = CycleReport()
    private val localReport: Report = LocalReport(localDepth)
    private val emitLine: (String) -> Unit = ::println
    private val notifications: Notifications = NotificationsImpl(emitLine)
    private val timeTakenEvent: (String, Duration) -> Unit = notifications::timeTakenEvent
    private val timer: Timer = EventTimer(timeTakenEvent, clock)
    private val timingReport: Report = TimingReport(timer)
    private val entryPointsReport: Report = EntryPointsReport()
    private val reports: List<Report> = listOf(
        staticContentReport,
        tableOfContentsReport,
        sourcesReport,
        binariesReport,
        entryPointsReport,
        cycleReport,
        localReport,
        graphReport
    )
    private val finalReports: List<Report> = listOf(
        timingReport
    )
    private val reportGenerator: ReportGenerator = ReportGeneratorImpl(reports, finalReports, outputDir)
    private val exec: Exec = ExecImpl()
    private val environment: Environment = EnvironmentImpl(files, outputDir, exec)
    private val commandRunner: CommandRunner = CommandRunnerImpl(timer, environment)
    private val configFileEvent: (Path) -> Unit = notifications::configFileEvent
    private val errorEvent: (ErrorDetail) -> Unit = notifications::errorEvent
    private val exit: (Int) -> Unit = System::exit
    private val fullAppTimeTakenEvent: (Duration) -> Unit = notifications::fullAppTimeTakenEvent
    val runner: Runnable = Runner(
        clock,
        observer,
        analyzer,
        reportGenerator,
        commandRunner,
        fullAppTimeTakenEvent,
        configFile,
        configFileEvent,
        errorEvent,
        exit,
        timer
    )
}
