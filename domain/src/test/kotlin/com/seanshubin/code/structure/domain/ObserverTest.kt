package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.filefinder.FileFinderImpl
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
        val tester = Tester(isSourceFile, filesOnDisk)

        // when
        val observations = tester.observer.makeObservations()

        // then
        val actual = tester.sourceFilesFound(observations)
        assertEquals(expected, actual)
    }

    class Tester(
        isSourceFile: (Path) -> Boolean,
        filesOnDisk: List<String>
    ) {
        val files = FakeFiles()
        val fileFinder = FileFinderImpl(files)
        val inputDir = Paths.get(".")
        val observer = ObserverImpl(inputDir, isSourceFile, fileFinder)

        init {
            filesOnDisk.forEach {
                files.fakeAddFile(it, "unused content")
            }
        }

        fun sourceFilesFound(observations: Observations): List<String> = observations.sourceFiles.map { it.toString() }
    }
}
