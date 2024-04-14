package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.injection.Dependencies
import com.seanshubin.code.structure.injection.Integrations
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class RegressionTest {
    @Test
    fun kotlinClass() {
        // given
        val paths = TestPaths("kotlin-class")
        val realClock = Clock.systemUTC()
        val emitLine = EmitLine()
        val regressionIntegrations = object : Integrations {
            override val clock: Clock = RememberingClock(paths.memoryDir, realClock)
            override val emitLine: (String) -> Unit = emitLine
            override val configBaseName: String = paths.configName
        }
        val dependencies = Dependencies(regressionIntegrations)

        // when
        dependencies.runner.run()

        // then
        seedExpectationIfNecessary(paths.expectedDir, paths.actualDir)
        val validationSummary = validateDirectoriesEqual(paths.expectedDir, paths.actualDir)
        assertEquals(0, validationSummary.regressionCount(), validationSummary.regressionString())
        assertEquals(null, dependencies.errorMessageHolder.errorMessage)
    }

    @Test
    fun kotlinJar() {
        // given
        val paths = TestPaths("kotlin-jar")
        val realClock = Clock.systemUTC()
        val emitLine = EmitLine()
        val regressionIntegrations = object : Integrations {
            override val clock: Clock = RememberingClock(paths.memoryDir, realClock)
            override val emitLine: (String) -> Unit = emitLine
            override val configBaseName: String = paths.configName
        }
        val dependencies = Dependencies(regressionIntegrations)

        // when
        dependencies.runner.run()

        // then
        seedExpectationIfNecessary(paths.expectedDir, paths.actualDir)
        val validationSummary = validateDirectoriesEqual(paths.expectedDir, paths.actualDir)
        assertEquals(0, validationSummary.regressionCount(), validationSummary.regressionString())
        assertEquals(null, dependencies.errorMessageHolder.errorMessage)
    }

    @Test
    fun elixir() {
        // given
        val paths = TestPaths("elixir")
        val realClock = Clock.systemUTC()
        val emitLine = EmitLine()
        val regressionIntegrations = object : Integrations {
            override val clock: Clock = RememberingClock(paths.memoryDir, realClock)
            override val emitLine: (String) -> Unit = emitLine
            override val configBaseName: String = paths.configName
        }
        val dependencies = Dependencies(regressionIntegrations)

        // when
        dependencies.runner.run()

        // then
        seedExpectationIfNecessary(paths.expectedDir, paths.actualDir)
        val validationSummary = validateDirectoriesEqual(paths.expectedDir, paths.actualDir)
        assertEquals(0, validationSummary.regressionCount(), validationSummary.regressionString())
        assertEquals(null, dependencies.errorMessageHolder.errorMessage)
    }

    class TestPaths(private val name: String) {
        val configName = "regression-test-$name"
        val memoryDir = Paths.get("regression-test", "memory", name)
        val expectedDir = Paths.get("regression-test", "expected", name)
        val actualDir = Paths.get("target", "regression-test", name)
    }

    private fun seedExpectationIfNecessary(expectedDir: Path, actualDir: Path) {
        if (Files.exists(expectedDir)) return
        recurseIntoFiles(actualDir) { relativePath ->
            val copyFrom = actualDir.resolve(relativePath)
            val copyTo = expectedDir.resolve(relativePath)
            val parentTo = copyTo.parent
            Files.createDirectories(parentTo)
            Files.copy(copyFrom, copyTo)
        }
    }

    private fun recurseIntoFiles(dir: Path, f: (Path) -> Unit) {
        recurseIntoFilesBase(dir, dir, f)
    }

    private fun recurseIntoFilesBase(baseDir: Path, dir: Path, f: (Path) -> Unit) {
        val list = Files.list(dir).toList()
        list.forEach { current ->
            if (Files.isDirectory(current)) {
                recurseIntoFilesBase(baseDir, current, f)
            } else if (Files.isRegularFile(current)) {
                val relativePath = baseDir.relativize(current)
                f(relativePath)
            }
        }
    }

    private fun validateDirectoriesEqual(expectedDir: Path, actualDir: Path): RegressionSummary {
        val missing = mutableListOf<Path>()
        val extra = mutableListOf<Path>()
        val differenceA = mutableListOf<Path>()
        val differenceB = mutableListOf<Path>()
        recurseIntoFiles(expectedDir) { relativePath ->
            val expectedFile = expectedDir.resolve(relativePath)
            val actualFile = actualDir.resolve(relativePath)
            if (Files.exists(actualFile)) {
                val expectedContent = Files.readString(expectedFile)
                val actualContent = Files.readString(actualFile)
                if (expectedContent != actualContent) {
                    differenceA.add(relativePath)
                }
            } else {
                missing.add(actualFile)
            }
        }
        recurseIntoFiles(actualDir) { relativePath ->
            val expectedFile = expectedDir.resolve(relativePath)
            val actualFile = actualDir.resolve(relativePath)
            if (Files.exists(expectedFile)) {
                val expectedContent = Files.readString(expectedFile)
                val actualContent = Files.readString(actualFile)
                if (expectedContent != actualContent) {
                    differenceB.add(relativePath)
                }
            } else {
                extra.add(expectedFile)
            }
        }
        val different = (differenceA + differenceB).distinct()
        return RegressionSummary(missing, extra, different)
    }

    class EmitLine : (String) -> Unit {
        val lines = mutableListOf<String>()
        override fun invoke(line: String) {
            lines.add(line)
        }
    }
}
