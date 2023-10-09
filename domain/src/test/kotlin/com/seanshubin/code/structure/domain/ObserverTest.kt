package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.binaryparser.BinaryDetail
import com.seanshubin.code.structure.binaryparser.BinaryParser
import com.seanshubin.code.structure.filefinder.FileFinderImpl
import com.seanshubin.code.structure.sourceparser.SourceDetail
import com.seanshubin.code.structure.sourceparser.SourceParser
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserverTest {
    @Test
    fun findSourceFiles() {
        // given
        val file1 = "base/dir/file-1.kt"
        val file2 = "base/dir/file-2.txt"
        val file3 = "base/dir/file-3.kt"
        val file4 = "base/dir/file-4.txt"


        val filesOnDisk = listOf(file1, file2, file3, file4)

        val sourceDetailMap = mapOf(
            file1 to makeSourceDetail(file1),
            file3 to makeSourceDetail(file3),
        )

        val expected = listOf(
            file1,
            file3,
        )

        val isSourceFile = { path: Path -> path.toString().endsWith(".kt") }
        val isBinaryFile = { _: Path -> false }
        val tester = Tester(isSourceFile, isBinaryFile, filesOnDisk, sourceDetailMap)

        // when
        val observations = tester.observer.makeObservations()

        // then
        val actual = tester.sourceFilesFound(observations)
        assertEquals(expected, actual)
    }

    private fun makeSourceDetail(name: String): SourceDetail = SourceDetail(
        Paths.get(name),
        "kotlin",
        emptyList(),
        emptyList()
    )

    class Tester(
        isSourceFile: (Path) -> Boolean,
        isBinaryFile: (Path) -> Boolean,
        filesOnDisk: List<String>,
        sourceDetailMap: Map<String, SourceDetail>
    ) {
        val files = FakeFiles()
        val fileFinder = FileFinderImpl(files)
        val inputDir = Paths.get(".")
        val sourcePrefix = ""
        val sourceParser = SourceParserStub(sourceDetailMap)
        val binaryParser = BinaryParserStub()
        val observer = ObserverImpl(
            inputDir,
            sourcePrefix,
            isSourceFile,
            isBinaryFile,
            fileFinder,
            sourceParser,
            binaryParser,
            files
        )

        init {
            filesOnDisk.forEach {
                files.fakeAddFile(it, "unused content")
            }
        }

        fun sourceFilesFound(observations: Observations): List<String> = observations.sourceFiles.map { it.toString() }
    }

    class SourceParserStub(private val map: Map<String, SourceDetail>) : SourceParser {
        override fun parseSource(path: Path, content: String): SourceDetail = map.getValue(path.toString())
    }

    class BinaryParserStub : BinaryParser {
        override fun parseBinary(path: Path, names: List<String>): List<BinaryDetail> {
            throw UnsupportedOperationException("not implemented")
        }
    }
}
