package com.seanshubin.code.structure.console

import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals

class RegressionTest {
    @Test
    fun kotlin() {
        // given
        val name = "regression-test-kotlin"
        val base = Paths.get(name)
        val expectedDir = base.resolve("expected")
        val memoryDir = base.resolve("memory")
        val actualDir = Paths.get("target", name)
        val args = arrayOf(name)
        val productionIntegrations = ProductionIntegrations()
        val emitLine = EmitLine()
        val regressionIntegrations = RegressionIntegrations(productionIntegrations, memoryDir, emitLine)
        val dependencies = Dependencies(regressionIntegrations, args)

        // when
        dependencies.runner.run()

        // then
        assertEquals("", "")
        seedExpectationIfNecessary(expectedDir, actualDir)
        val validationSummary = validateDirectoriesEqual(expectedDir, actualDir)
        assertEquals(0, validationSummary.regressionCount(), validationSummary.regressionString())
        assertEquals(0, dependencies.exitCodeHolder.exitCode)
    }

    @Test
    fun elixir() {
        // given
        val name = "regression-test-elixir"
        val base = Paths.get(name)
        val expectedDir = base.resolve("expected")
        val memoryDir = base.resolve("memory")
        val actualDir = Paths.get("target", name)
        val args = arrayOf(name)
        val productionIntegrations = ProductionIntegrations()
        val emitLine = EmitLine()
        val regressionIntegrations = RegressionIntegrations(productionIntegrations, memoryDir, emitLine)
        val dependencies = Dependencies(regressionIntegrations, args)

        // when
        dependencies.runner.run()

        // then
        seedExpectationIfNecessary(expectedDir, actualDir)
        val validationSummary = validateDirectoriesEqual(expectedDir, actualDir)
        assertEquals(0, validationSummary.regressionCount(), validationSummary.regressionString())
        assertEquals(0, dependencies.exitCodeHolder.exitCode)
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

    private fun validateDirectoriesEqual(expectedDir: Path, actualDir: Path):RegressionSummary {
        val missing = mutableListOf<Path>()
        val extra = mutableListOf<Path>()
        val differenceA = mutableListOf<Path>()
        val differenceB = mutableListOf<Path>()
        recurseIntoFiles(expectedDir) { relativePath ->
            val expectedFile = expectedDir.resolve(relativePath)
            val actualFile = actualDir.resolve(relativePath)
            if(Files.exists(actualFile)) {
                val expectedContent = Files.readString(expectedFile)
                val actualContent = Files.readString(actualFile)
                if(expectedContent != actualContent){
                    differenceA.add(relativePath)
                }
            } else {
                missing.add(actualFile)
            }
        }
        recurseIntoFiles(actualDir) { relativePath ->
            val expectedFile = expectedDir.resolve(relativePath)
            val actualFile = actualDir.resolve(relativePath)
            if(Files.exists(expectedFile)){
                val expectedContent = Files.readString(expectedFile)
                val actualContent = Files.readString(actualFile)
                if(expectedContent != actualContent){
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