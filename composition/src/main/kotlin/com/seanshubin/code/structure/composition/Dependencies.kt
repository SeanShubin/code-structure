package com.seanshubin.code.structure.composition

import com.seanshubin.code.structure.beamformat.BeamParser
import com.seanshubin.code.structure.beamformat.BeamParserImpl
import com.seanshubin.code.structure.clojuresyntax.ClojureParser
import com.seanshubin.code.structure.clojuresyntax.ClojureParserImpl
import com.seanshubin.code.structure.commands.CommandRunner
import com.seanshubin.code.structure.commands.CommandRunnerImpl
import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.cycle.CycleAlgorithm
import com.seanshubin.code.structure.cycle.CycleAlgorithmTarjan
import com.seanshubin.code.structure.elixirsyntax.ElixirParser
import com.seanshubin.code.structure.elixirsyntax.ElixirParserImpl
import com.seanshubin.code.structure.events.EventTimer
import com.seanshubin.code.structure.events.Notifications
import com.seanshubin.code.structure.events.NotificationsImpl
import com.seanshubin.code.structure.events.Timer
import com.seanshubin.code.structure.exec.Exec
import com.seanshubin.code.structure.filefinder.*
import com.seanshubin.code.structure.javasyntax.JavaParser
import com.seanshubin.code.structure.javasyntax.JavaParserImpl
import com.seanshubin.code.structure.jvmformat.*
import com.seanshubin.code.structure.kotlinsyntax.KotlinParser
import com.seanshubin.code.structure.kotlinsyntax.KotlinParserImpl
import com.seanshubin.code.structure.model.Summary
import com.seanshubin.code.structure.nameparser.NameParser
import com.seanshubin.code.structure.pipeline.*
import com.seanshubin.code.structure.relationparser.RelationParser
import com.seanshubin.code.structure.reports.*
import com.seanshubin.code.structure.runtime.*
import com.seanshubin.code.structure.scalasyntax.ScalaParser
import com.seanshubin.code.structure.scalasyntax.ScalaParserImpl
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptNameParser
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptNameParserImpl
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptRelationParser
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptRelationParserImpl
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.time.Clock
import java.time.Duration

class Dependencies(
    integrations: Integrations,
    config: Configuration
) {
    private val charset: Charset = StandardCharsets.UTF_8
    private val files: FilesContract = integrations.files
    private val clock: Clock = integrations.clock
    private val exec: Exec = integrations.exec
    private val countAsErrors: CountAsErrors = config.countAsErrors
    private val maximumAllowedErrorCount: Int = config.maximumAllowedErrorCount
    private val inputDir = config.inputDir
    private val outputDir = config.outputDir
    private val useObservationsCache = config.useObservationsCache
    private val includeJvmDynamicInvocations = config.includeJvmDynamicInvocations
    private val sourcePrefix = config.sourcePrefix
    private val sourceFileIncludeRegexPatterns: List<String> = config.sourceFileIncludeRegexPatterns
    private val sourceFileExcludeRegexPatterns: List<String> = config.sourceFileExcludeRegexPatterns
    private val filterStats: FilterStats = FilterStatsImpl()
    private val isSourceFile: (Path) -> Boolean = RegexFileMatcherWithStats(
        inputDir,
        sourceFileIncludeRegexPatterns,
        sourceFileExcludeRegexPatterns,
        filterStats,
        "source-files"
    )
    private val nodeLimitForGraph: Int = config.nodeLimitForGraph
    private val binaryFileIncludeRegexPatterns: List<String> = config.binaryFileIncludeRegexPatterns
    private val binaryFileExcludeRegexPatterns: List<String> = config.binaryFileExcludeRegexPatterns
    private val isBinaryFile: (Path) -> Boolean = RegexFileMatcherWithStats(
        inputDir,
        binaryFileIncludeRegexPatterns,
        binaryFileExcludeRegexPatterns,
        filterStats,
        "binary-files"
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
    private val tableOfContentsReport: Report = TableOfContentsReport(nodeLimitForGraph)
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
    private val qualityMetricsReport: Report = QualityMetricsReport()
    private val qualityMetricsInDirectCycleReport: Report = QualityMetricsInDirectCycleReport()
    private val qualityMetricsInGroupCycleReport: Report = QualityMetricsInGroupCycleReport()
    private val qualityMetricsAncestorDependsOnDescendantReport: Report =
        QualityMetricsLineageReport(
            "ancestorDependsOnDescendant",
            { it.ancestorDependsOnDescendant }
        ) { ancestor, descendant ->
            mapOf("ancestor" to ancestor, "descendant" to descendant)
        }

    private val qualityMetricsDescendantDependsOnAncestorReport: Report =
        QualityMetricsLineageReport(
            "descendantDependsOnAncestor",
            { it.descendantDependsOnAncestor }
        ) { descendant, ancestor ->
            mapOf("descendant" to descendant, "ancestor" to ancestor)
        }
    private val filterStatisticsIndexReport: Report = FilterStatisticsIndexReport(filterStats)
    private val filterStatisticsCategoryReportSourceFiles: Report =
        FilterStatisticsCategoryReport(filterStats, "source-files")
    private val filterStatisticsCategoryReportBinaryFiles: Report =
        FilterStatisticsCategoryReport(filterStats, "binary-files")
    private val filterStatisticsByFileReportSourceFiles: Report =
        FilterStatisticsByFileReport(filterStats, "source-files")
    private val filterStatisticsByPatternReportSourceFiles: Report =
        FilterStatisticsByPatternReport(filterStats, "source-files")
    private val filterStatisticsUnmatchedFilesReportSourceFiles: Report =
        FilterStatisticsUnmatchedFilesReport(filterStats, "source-files")
    private val filterStatisticsUnusedPatternsReportSourceFiles: Report =
        FilterStatisticsUnusedPatternsReport(filterStats, "source-files")
    private val filterStatisticsMultiPatternFilesReportSourceFiles: Report =
        FilterStatisticsMultiPatternFilesReport(filterStats, "source-files")
    private val filterStatisticsByFileReportBinaryFiles: Report =
        FilterStatisticsByFileReport(filterStats, "binary-files")
    private val filterStatisticsByPatternReportBinaryFiles: Report =
        FilterStatisticsByPatternReport(filterStats, "binary-files")
    private val filterStatisticsUnmatchedFilesReportBinaryFiles: Report =
        FilterStatisticsUnmatchedFilesReport(filterStats, "binary-files")
    private val filterStatisticsUnusedPatternsReportBinaryFiles: Report =
        FilterStatisticsUnusedPatternsReport(filterStats, "binary-files")
    private val filterStatisticsMultiPatternFilesReportBinaryFiles: Report =
        FilterStatisticsMultiPatternFilesReport(filterStats, "binary-files")
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
        graphReport,
        qualityMetricsReport,
        qualityMetricsInDirectCycleReport,
        qualityMetricsInGroupCycleReport,
        qualityMetricsAncestorDependsOnDescendantReport,
        qualityMetricsDescendantDependsOnAncestorReport,
        filterStatisticsIndexReport,
        filterStatisticsCategoryReportSourceFiles,
        filterStatisticsCategoryReportBinaryFiles,
        filterStatisticsByFileReportSourceFiles,
        filterStatisticsByPatternReportSourceFiles,
        filterStatisticsUnmatchedFilesReportSourceFiles,
        filterStatisticsUnusedPatternsReportSourceFiles,
        filterStatisticsMultiPatternFilesReportSourceFiles,
        filterStatisticsByFileReportBinaryFiles,
        filterStatisticsByPatternReportBinaryFiles,
        filterStatisticsUnmatchedFilesReportBinaryFiles,
        filterStatisticsUnusedPatternsReportBinaryFiles,
        filterStatisticsMultiPatternFilesReportBinaryFiles
    )
    private val finalReports: List<Report> = listOf(
        timingReport
    )
    private val reportGenerator: ReportGenerator = ReportGeneratorImpl(reports, finalReports, outputDir, timer)
    private val environment: Environment = EnvironmentImpl(files, outputDir, exec)
    private val commandRunner: CommandRunner = CommandRunnerImpl(environment)
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
        summaryEvent,
        timer,
        errorMessageHolder
    )
}
