package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.beamformat.BeamParserImpl
import com.seanshubin.code.structure.relationparser.RelationParser
import com.seanshubin.code.structure.relationparser.RelationParserRepository
import com.seanshubin.code.structure.config.Configuration
import com.seanshubin.code.structure.config.JsonFileConfiguration
import com.seanshubin.code.structure.config.TypeUtil.coerceToBoolean
import com.seanshubin.code.structure.config.TypeUtil.coerceToInt
import com.seanshubin.code.structure.config.TypeUtil.coerceToListOfString
import com.seanshubin.code.structure.config.TypeUtil.coerceToPath
import com.seanshubin.code.structure.config.TypeUtil.coerceToString
import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.contract.delegate.FilesDelegate
import com.seanshubin.code.structure.domain.*
import com.seanshubin.code.structure.elixirsyntax.ElixirParser
import com.seanshubin.code.structure.elixirsyntax.ElixirParserImpl
import com.seanshubin.code.structure.exec.Exec
import com.seanshubin.code.structure.exec.ExecImpl
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.filefinder.FileFinderImpl
import com.seanshubin.code.structure.filefinder.RegexFileMatcher
import com.seanshubin.code.structure.jvmformat.*
import com.seanshubin.code.structure.kotlinsyntax.KotlinParser
import com.seanshubin.code.structure.kotlinsyntax.KotlinParserImpl
import com.seanshubin.code.structure.nameparser.NameParser
import com.seanshubin.code.structure.nameparser.NameParserRepository
import com.seanshubin.code.structure.scalasyntax.ScalaParser
import com.seanshubin.code.structure.scalasyntax.ScalaParserImpl
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Clock
import java.time.Duration

class Dependencies(integrations: Integrations, args: Array<String>) {
    private val configBaseName = if (args.isEmpty() || args[0].isBlank()) {
        "code-structure"
    } else {
        args[0]
    }
    private val configFile = Paths.get("$configBaseName-config.json")
    private val configuredErrorsFile = Paths.get("$configBaseName-existing-errors.json")
    private val files: FilesContract = FilesDelegate
    private val config: Configuration = JsonFileConfiguration(files, configFile)
    private val clock: Clock = integrations.clock
    private val failConditions: FailConditions = FailConditions(
        directCycle =
        config.load(listOf("failConditions", "directCycle"), true).coerceToBoolean(),
        groupCycle =
        config.load(listOf("failConditions", "groupCycle"), true).coerceToBoolean(),
        ancestorDependsOnDescendant =
        config.load(listOf("failConditions", "ancestorDependsOnDescendant"), true).coerceToBoolean(),
        descendantDependsOnAncestor =
        config.load(listOf("failConditions", "descendantDependsOnAncestor"), true).coerceToBoolean(),
    )
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
    private val nodeLimitMainGraph: Int = config.load(listOf("nodeLimitMainGraph"), 100).coerceToInt()
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
    private val kotlinParser: KotlinParser = KotlinParserImpl(inputDir)
    private val elixirParser: ElixirParser = ElixirParserImpl(inputDir)
    private val scalaParser: ScalaParser = ScalaParserImpl(inputDir)
    private val nameParserRepository: NameParserRepository = NameParserRepositoryImpl(
        kotlinParser,
        elixirParser,
        scalaParser
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
    private val relationParserRepository: RelationParserRepository = RelationParserRepositoryImpl(
        classParser,
        beamParser
    )
    private val nameParser: NameParser = nameParserRepository.lookupByLanguage(language)
    private val relationParser: RelationParser = relationParserRepository.lookupByBytecodeFormat(bytecodeFormat)
    private val observer: Observer = ObserverImpl(
        inputDir,
        configuredErrorsFile,
        sourcePrefix,
        isSourceFile,
        isBinaryFile,
        fileFinder,
        nameParser,
        relationParser,
        files
    )
    private val analyzer: Analyzer = AnalyzerImpl()
    private val validator: Validator = ValidatorImpl()
    private val staticContentReport: Report = StaticContentReport()
    private val sourcesReport: Report = SourcesReport()
    private val tableOfContentsReport: Report = TableOfContentsReport()
    private val binariesReport: Report = BinariesReport()
    private val graphReport: Report = GraphReport(nodeLimitMainGraph)
    private val directCycleReport: Report = DirectCycleReport()
    private val groupCycleReport: Report = GroupCycleReport()
    private val lineageReportAncestorDescendant: Report =
        LineageReport(Page.lineageAncestorDescendant) { it.ancestorDependsOnDescendant }
    private val lineageReportDescendantAncestor: Report =
        LineageReport(Page.lineageDescendantAncestor) { it.descendantDependsOnAncestor }
    private val localReport: Report = LocalReport(localDepth)
    private val emitLine: (String) -> Unit = integrations.emitLine
    private val notifications: Notifications = NotificationsImpl(emitLine)
    private val timeTakenEvent: (String, Duration) -> Unit = notifications::timeTakenEvent
    private val timer: Timer = EventTimer(timeTakenEvent, clock)
    private val timingReport: Report = TimingReport(timer)
    private val entryPointsReport: Report = EntryPointsReport()
    private val groupReport: Report = GroupReport()
    private val reports: List<Report> = listOf(
        staticContentReport,
        tableOfContentsReport,
        sourcesReport,
        binariesReport,
        entryPointsReport,
        directCycleReport,
        groupCycleReport,
        lineageReportAncestorDescendant,
        lineageReportDescendantAncestor,
        groupReport,
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
    private val errorReportEvent: (List<String>) -> Unit = notifications::errorReportEvent
    private val fullAppTimeTakenEvent: (Duration) -> Unit = notifications::fullAppTimeTakenEvent
    private val errorHandler: ErrorHandler = ErrorHandlerImpl(files, configuredErrorsFile, errorReportEvent)
    private val summaryEvent: (Summary) -> Unit = notifications::summaryEvent
    val exitCodeHolder: ExitCodeHolder = ExitCodeHolderImpl()
    val runner: Runnable = Runner(
        clock,
        observer,
        analyzer,
        validator,
        reportGenerator,
        commandRunner,
        fullAppTimeTakenEvent,
        configFile,
        configFileEvent,
        summaryEvent,
        timer,
        exitCodeHolder,
        errorHandler,
        failConditions
    )
}
