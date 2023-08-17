package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.test.FilesNotImplemented
import com.seanshubin.code.structure.filefinder.BiPredicateAdapterUseFirst
import com.seanshubin.code.structure.filefinder.FileFinder
import com.seanshubin.code.structure.filefinder.FileFinderImpl
import java.nio.file.FileVisitOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.function.BiPredicate
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals

class RunnerTest {
    @Test
    fun run() {
        // given
        val inputDirName = "/input/dir_a"
        val startTimeMillis = 10000
        val endTimeMillis = 12345
        val findResult = listOf(
            "dir_b/dir_c/file_e.source",
            "dir_b/dir_d/file_f.source"
        )
        val isSourceFile = { _: Path -> true }
        val tester = Tester(
            inputDirName,
            startTimeMillis,
            endTimeMillis,
            isSourceFile,
            findResult
        )
        val expectedDuration = Duration.ofMillis(2345)

        // when
        tester.runner.run()

        // then
        assertEquals(1, tester.files.invocations.size)

        val filesInvocation = tester.files.invocations[0]
        assertEquals("find", filesInvocation["name"])
        assertEquals("/input/dir_a", filesInvocation["start"])
        assertEquals(Int.MAX_VALUE, filesInvocation["maxDepth"])
        val matcher = filesInvocation["matcher"] as BiPredicateAdapterUseFirst<*, *>
        assertEquals(isSourceFile, matcher.delegate)
        val options = filesInvocation["options"] as Array<*>
        assertEquals(0, options.size)

        assertEquals(2, tester.reportGenerator.invocations.size)

        val sourceInvocation = tester.reportGenerator.invocations[0]
        assertEquals("sourceFiles", sourceInvocation["name"])
        assertEquals(findResult, sourceInvocation["sourceFiles"])

        val indexInvocation = tester.reportGenerator.invocations[1]
        assertEquals("index", indexInvocation["name"])
        assertEquals(expectedDuration, indexInvocation["duration"])
    }

    class Tester(
        inputDirName: String,
        startTimeMillis: Int,
        endTimeMillis: Int,
        isSourceFile: (Path) -> Boolean,
        findResult: List<String>
    ) {
        val inputDir: Path = Paths.get(inputDirName)
        val clock: Clock = ClockStub(startTimeMillis.toLong(), endTimeMillis.toLong())
        val files: FilesStub = FilesStub(findResult.map(Paths::get))
        val fileFinder: FileFinder = FileFinderImpl(files)
        val reportGenerator: ReportGeneratorStub = ReportGeneratorStub()
        val runner = Runner(clock, inputDir, isSourceFile, fileFinder, reportGenerator)
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

    class FilesStub(private val findResult: List<Path>) : FilesNotImplemented() {
        val invocations = mutableListOf<Map<String, Any>>()
        override fun find(
            start: Path,
            maxDepth: Int,
            matcher: BiPredicate<Path, BasicFileAttributes>,
            vararg options: FileVisitOption
        ): Stream<Path> {
            invocations.add(
                mapOf(
                    "name" to "find",
                    "start" to start.toString(),
                    "maxDepth" to maxDepth,
                    "matcher" to matcher,
                    "options" to options
                )
            )
            return findResult.stream()
        }
    }

    class ReportGeneratorStub() : ReportGenerator {
        val invocations = mutableListOf<Map<String, Any>>()
        override fun sourceFiles(sourceFiles: List<Path>) {
            invocations.add(
                mapOf(
                    "name" to "sourceFiles",
                    "sourceFiles" to sourceFiles.map{it.toString()}
                )
            )
        }

        override fun index(duration: Duration) {
            invocations.add(
                mapOf(
                    "name" to "index",
                    "duration" to duration
                )
            )
        }
    }
}
