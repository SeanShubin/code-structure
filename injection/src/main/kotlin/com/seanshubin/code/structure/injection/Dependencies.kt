package com.seanshubin.code.structure.injection

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.beamformat.BeamParserImpl
import com.seanshubin.code.structure.clojuresyntax.ClojureParser
import com.seanshubin.code.structure.clojuresyntax.ClojureParserImpl
import com.seanshubin.code.structure.config.Configuration
import com.seanshubin.code.structure.config.JsonFileConfiguration
import com.seanshubin.code.structure.config.TypeUtil.coerceToBoolean
import com.seanshubin.code.structure.config.TypeUtil.coerceToInt
import com.seanshubin.code.structure.config.TypeUtil.coerceToListOfString
import com.seanshubin.code.structure.config.TypeUtil.coerceToPath
import com.seanshubin.code.structure.config.TypeUtil.coerceToString
import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.contract.delegate.FilesDelegate
import com.seanshubin.code.structure.cycle.CycleAlgorithm
import com.seanshubin.code.structure.cycle.CycleAlgorithmTarjan
import com.seanshubin.code.structure.domain.*
import com.seanshubin.code.structure.elixirsyntax.ElixirParser
import com.seanshubin.code.structure.elixirsyntax.ElixirParserImpl
import com.seanshubin.code.structure.exec.Exec
import com.seanshubin.code.structure.exec.ExecImpl
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.filefinder.FileFinderImpl
import com.seanshubin.code.structure.filefinder.RegexFileMatcher
import com.seanshubin.code.structure.javasyntax.JavaParser
import com.seanshubin.code.structure.javasyntax.JavaParserImpl
import com.seanshubin.code.structure.jvmformat.*
import com.seanshubin.code.structure.kotlinsyntax.KotlinParser
import com.seanshubin.code.structure.kotlinsyntax.KotlinParserImpl
import com.seanshubin.code.structure.nameparser.NameParser
import com.seanshubin.code.structure.relationparser.RelationParser
import com.seanshubin.code.structure.scalasyntax.ScalaParser
import com.seanshubin.code.structure.scalasyntax.ScalaParserImpl
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptNameParser
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptNameParserImpl
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptRelationParser
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptRelationParserImpl
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Clock
import java.time.Duration

class Dependencies(integrations: Integrations) {
    private val charset: Charset = StandardCharsets.UTF_8
    private val configBaseName: String = integrations.configBaseName
    private val configFile = Paths.get("$configBaseName-config.json")
    private val files: FilesContract = FilesDelegate
    private val config: Configuration = JsonFileConfiguration(files, configFile)
    private val clock: Clock = integrations.clock
    private val countAsErrors: CountAsErrors = CountAsErrors(
        inDirectCycle =
        config.load(listOf("countAsErrors", "inDirectCycle"), true).coerceToBoolean(),
        inGroupCycle =
        config.load(listOf("countAsErrors", "inGroupCycle"), true).coerceToBoolean(),
        ancestorDependsOnDescendant =
        config.load(listOf("countAsErrors", "ancestorDependsOnDescendant"), true).coerceToBoolean(),
        descendantDependsOnAncestor =
        config.load(listOf("countAsErrors", "descendantDependsOnAncestor"), true).coerceToBoolean(),
    )
    private val maximumAllowedErrorCount: Int = config.load(listOf("maximumAllowedErrorCount"), 0).coerceToInt()
    private val inputDir = config.load(listOf("inputDir"), ".").coerceToPath()
    private val outputDir = config.load(listOf("outputDir"), "generated/code-structure").coerceToPath()
    private val useObservationsCache = config.load(listOf("useObservationsCache"), false).coerceToBoolean()
    private val includeJvmDynamicInvocations =
        config.load(listOf("includeJvmDynamicInvocations"), false).coerceToBoolean()
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
    private val nodeLimitForGraph: Int = config.load(listOf("nodeLimitForGraph"), 50).coerceToInt()
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
    private val javaParser: JavaParser = JavaParserImpl(inputDir)
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
    private val classParser: ClassParser =
        ClassParserImpl(inputDir, byteSequenceLoader, classInfoLoader, includeJvmDynamicInvocations)
    private val beamParser: BeamParser = BeamParserImpl(files, inputDir)
    private val typeScriptRelationParser: TypeScriptRelationParser = TypeScriptRelationParserImpl(
        files, charset
    )
    private val typeScriptNameParser: TypeScriptNameParser = TypeScriptNameParserImpl()
    private val clojureParser: ClojureParser = ClojureParserImpl(inputDir)
    private val nameParser: NameParser = DynamicNameParser(
        kotlinParser,
        elixirParser,
        scalaParser,
        javaParser,
        typeScriptNameParser,
        clojureParser
    )
    private val relationParser: RelationParser =
        DynamicRelationParser(classParser, beamParser, typeScriptRelationParser)
    private val observer: Observer = ObserverImpl(
        inputDir,
        sourcePrefix,
        isSourceFile,
        isBinaryFile,
        fileFinder,
        nameParser,
        relationParser,
        files,
        outputDir,
        useObservationsCache
    )
    private val emitLine: (String) -> Unit = integrations.emitLine
    private val notifications: Notifications = NotificationsImpl(emitLine)
    private val timeTakenEvent: (String, Duration) -> Unit = notifications::timeTakenEvent
    private val timer: Timer = EventTimer(timeTakenEvent, clock)
    private val cycleAlgorithm: CycleAlgorithm = CycleAlgorithmTarjan()
    private val analyzer: Analyzer = AnalyzerImpl(timer, cycleAlgorithm, countAsErrors, maximumAllowedErrorCount)
    private val validator: Validator = ValidatorImpl()
    private val staticContentReport: Report = StaticContentReport()
    private val sourcesReport: Report = SourcesReport()
    private val tableOfContentsReport: Report = TableOfContentsReport()
    private val binariesReport: Report = BinariesReport()
    private val missingBinariesReport: Report = MissingBinariesReport()
    private val dependenciesReport: Report = DependenciesReport()
    private val graphReport: Report = GraphReport(nodeLimitForGraph)
    private val directCycleReport: Report = DirectCycleReport(nodeLimitForGraph)
    private val groupCycleReport: Report = GroupCycleReport(nodeLimitForGraph)
    private val lineageReportAncestorDescendant: Report =
        LineageReport(Page.lineageAncestorDescendant) { it.ancestorDependsOnDescendant }
    private val lineageReportDescendantAncestor: Report =
        LineageReport(Page.lineageDescendantAncestor) { it.descendantDependsOnAncestor }
    private val codeUnitsReport: Report = CodeUnitsReport()
    private val timingReport: Report = TimingReport(timer)
    private val entryPointsReport: Report = EntryPointsReport()
    private val groupReport: Report = GroupReport(nodeLimitForGraph)
    private val reports: List<Report> = listOf(
        staticContentReport,
        tableOfContentsReport,
        sourcesReport,
        binariesReport,
        missingBinariesReport,
        dependenciesReport,
        entryPointsReport,
        groupReport,
        directCycleReport,
        groupCycleReport,
        lineageReportAncestorDescendant,
        lineageReportDescendantAncestor,
        codeUnitsReport,
        graphReport
    )
    private val finalReports: List<Report> = listOf(
        timingReport
    )
    private val reportGenerator: ReportGenerator = ReportGeneratorImpl(reports, finalReports, outputDir, timer)
    private val exec: Exec = ExecImpl()
    private val environment: Environment = EnvironmentImpl(files, outputDir, exec)
    private val commandRunner: CommandRunner = CommandRunnerImpl(timer, environment)
    private val configFileEvent: (Path) -> Unit = notifications::configFileEvent
    private val fullAppTimeTakenEvent: (Duration) -> Unit = notifications::fullAppTimeTakenEvent
    private val summaryEvent: (Summary) -> Unit = notifications::summaryEvent
    val errorMessageHolder: ErrorMessageHolder = ErrorMessageHolderImpl()
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
        errorMessageHolder
    )
}
