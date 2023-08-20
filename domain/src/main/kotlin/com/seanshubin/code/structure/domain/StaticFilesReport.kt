package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.io.IoUtil.consumeLines
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class StaticFilesReport : Report {
    private val classLoader = javaClass.classLoader
    override fun generate(reportDir: Path, analysis: Analysis): List<CreateFileCommand> {
        return listOf(
            fromResource(reportDir, "reset.css"),
            fromResource(reportDir, "code-structure.css")
        )
    }

    private fun fromResource(reportDir: Path, name: String): CreateFileCommand {
        val inputStream = classLoader.getResourceAsStream(name)
            ?: throw RuntimeException("Unable to load resource named '$name'")
        val lines = inputStream.consumeLines(StandardCharsets.UTF_8)
        val path = reportDir.resolve(name)
        return CreateFileCommand(path, lines)
    }
}
