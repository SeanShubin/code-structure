package com.seanshubin.code.structure.domain

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
        val tester = Tester(
            startTimeMillis,
            endTimeMillis,
            sourceFiles
        )
        val expectedIndexReports = listOf(
            Duration.ofMillis(2345)
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
        assertEquals(expectedIndexReports, tester.generateIndexInvocations())
    }

    class Tester(
        startTimeMillis: Int,
        endTimeMillis: Int,
        sourceFiles: List<String>
    ) {
        val clock: Clock = ClockStub(startTimeMillis.toLong(), endTimeMillis.toLong())
        val reportGenerator: ReportGeneratorStub = ReportGeneratorStub()
        val sourceFilePaths = sourceFiles.map { Paths.get(it) }
        val observations = Observations(sourceFilePaths)
        val observer: ObserverStub = ObserverStub(observations)
        val analyzer: AnalyzerStub = AnalyzerStub()
        val runner = Runner(clock, observer, analyzer, reportGenerator)
        fun generateReportInvocations(): List<List<String>> =
            reportGenerator.generateReportsInvocations.map { analysis ->
                analysis.observations.sourceFiles.map { it.toString() }
            }

        fun generateIndexInvocations(): List<Duration> = reportGenerator.generateIndexInvocations
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

    class ReportGeneratorStub : ReportGenerator {
        val generateReportsInvocations = mutableListOf<Analysis>()
        val generateIndexInvocations = mutableListOf<Duration>()
        override fun generateReports(analysis: Analysis) {
            generateReportsInvocations.add(analysis)
        }

        override fun generateIndex(duration: Duration) {
            generateIndexInvocations.add(duration)
        }
    }
}
