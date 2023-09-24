package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.TestUtil.exactlyOne
import com.seanshubin.code.structure.parser.SourceDetail
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals

class RunnerTest {
    @Test
    fun run() {
        // given
        val startTimeMillis = 10000
        val endTimeMillis = 12345
        val sourceFiles = listOf(
            "dir/file-a.txt",
            "dir/file-b.txt",
            "dir/file-c.txt"
        )
        val inputDirName = "input"
        val reportPathName = "generated/report.html"
        val reportLine = "the report contents"
        val tester = Tester(
            startTimeMillis,
            endTimeMillis,
            sourceFiles,
            inputDirName,
            reportPathName,
            reportLine
        )
        val expectedReports = listOf(
            listOf(
                "dir/file-a.txt",
                "dir/file-b.txt",
                "dir/file-c.txt"
            )
        )

        // when
        tester.runner.run()

        // then
        assertEquals(expectedReports, tester.generateReportInvocations())
        val actualCommand = tester.commands().exactlyOne() as CreateFileCommand
        assertEquals(Paths.get("generated/report.html"), actualCommand.path)
        assertEquals(listOf("the report contents"), actualCommand.lines)

        val actualDuration = tester.durations().exactlyOne()
        assertEquals(Duration.ofMillis(2345), actualDuration)
    }

    class Tester(
        startTimeMillis: Int,
        endTimeMillis: Int,
        sourceFiles: List<String>,
        inputDirName: String,
        reportPathName: String,
        reportLine: String
    ) {
        val inputDir = Paths.get(inputDirName)
        val sourcePrefix = ""

        val clock: Clock = ClockStub(startTimeMillis.toLong(), endTimeMillis.toLong())
        val reportPath = Paths.get(reportPathName)
        val reportLines = listOf(reportLine)
        val createFileCommand = CreateFileCommand(reportPath, reportLines)
        val reportGenerator: ReportGeneratorStub = ReportGeneratorStub(createFileCommand)
        val sourceFilePaths = sourceFiles.map { Paths.get(it) }
        val sourceDetailByPath = emptyMap<Path, SourceDetail>()
        val observations = Observations(inputDir, sourcePrefix, sourceFilePaths, sourceDetailByPath)
        val observer: ObserverStub = ObserverStub(observations)
        val analyzer: AnalyzerStub = AnalyzerStub()
        val commandRunner = CommandRunnerStub()
        val durationEvent = DurationEventStub()
        val runner = Runner(clock, observer, analyzer, reportGenerator, commandRunner, durationEvent)
        fun generateReportInvocations(): List<List<String>> =
            reportGenerator.generateReportsInvocations.map { analysis ->
                analysis.observations.sourceFiles.map { it.toString() }
            }

        fun commands(): List<Command> = commandRunner.commands
        fun durations(): List<Duration> = durationEvent.durations
    }

    class ObserverStub(val observations: Observations) : Observer {
        override fun makeObservations(): Observations = observations
    }

    class AnalyzerStub : Analyzer {
        override fun analyze(observations: Observations): Analysis {
            return Analysis(observations)
        }
    }

    class ClockStub(vararg val millisArray: Long) : Clock() {
        var index = 0
        override fun instant(): Instant =
            Instant.ofEpochMilli(millisArray[index++])

        override fun withZone(zone: ZoneId?): Clock {
            throw UnsupportedOperationException("not implemented")
        }

        override fun getZone(): ZoneId {
            throw UnsupportedOperationException("not implemented")
        }
    }

    class ReportGeneratorStub(val createFileCommand: CreateFileCommand) : ReportGenerator {
        val generateReportsInvocations = mutableListOf<Analysis>()

        override fun generateReports(analysis: Analysis): List<CreateFileCommand> {
            generateReportsInvocations.add(analysis)
            return listOf(createFileCommand)
        }
    }

    class CommandRunnerStub : CommandRunner {
        val commands = mutableListOf<Command>()
        override fun execute(command: Command) {
            commands.add(command)
        }
    }

    class DurationEventStub : (Duration) -> Unit {
        val durations = mutableListOf<Duration>()
        override fun invoke(duration: Duration) {
            durations.add(duration)
        }
    }
}
