package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.io.IoUtil.consumeLines
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class StaticContentReport : Report {
    private val classLoader = javaClass.classLoader
    override fun generate(reportDir: Path, validated: Validated): List<CreateFileCommand> {
        return listOf(
            fromResource(reportDir, "reset.css"),
            fromResource(reportDir, "code-structure.css"),
            fromResource(reportDir, "_index.html")
        )
    }

    private fun fromResource(reportDir: Path, name: String): CreateFileCommand {
        val resourceName = "static-content/$name"
        val inputStream = classLoader.getResourceAsStream(resourceName)
            ?: throw RuntimeException("Unable to load resource named '$name'")
        val lines = inputStream.consumeLines(StandardCharsets.UTF_8)
        val path = reportDir.resolve(name)
        return CreateFileCommand(path, lines)
    }
}
