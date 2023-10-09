package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.bytecodeformat.BinaryDetail
import com.seanshubin.code.structure.bytecodeformat.BinaryParser
import com.seanshubin.code.structure.filefinder.FileFinderImpl
import com.seanshubin.code.structure.parser.SourceParser
import com.seanshubin.code.structure.parser.SourceDetail
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserverTest {
    @Test
    fun findSourceFiles() {
        // given
        val filesOnDisk = listOf(
            "base/dir/file-1.kt",
            "base/dir/file-2.txt",
            "base/dir/file-3.kt",
            "base/dir/file-4.txt",
        )

        val expected = listOf(
            "base/dir/file-1.kt",
            "base/dir/file-3.kt",
        )

        val isSourceFile = { path: Path -> path.toString().endsWith(".kt") }
        val isBinaryFile = { _:Path -> false }
        val tester = Tester(isSourceFile, isBinaryFile, filesOnDisk)

        // when
        val observations = tester.observer.makeObservations()

        // then
        val actual = tester.sourceFilesFound(observations)
        assertEquals(expected, actual)
    }

    class Tester(
        isSourceFile: (Path) -> Boolean,
        isBinaryFile:(Path)->Boolean,
        filesOnDisk: List<String>
    ) {
        val files = FakeFiles()
        val fileFinder = FileFinderImpl(files)
        val inputDir = Paths.get(".")
        val sourcePrefix = ""
        val sourceParser = SourceParserStub()
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

    class SourceParserStub : SourceParser {
        override fun parseSource(path: Path, content: String): SourceDetail {
            throw UnsupportedOperationException("not implemented")
        }
    }
    class BinaryParserStub : BinaryParser {
        override fun parseBinary(path: Path, names:List<String>): List<BinaryDetail> {
            throw UnsupportedOperationException("not implemented")
        }
    }
}
