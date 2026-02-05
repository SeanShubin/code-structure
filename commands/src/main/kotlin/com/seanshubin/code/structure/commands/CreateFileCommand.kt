package com.seanshubin.code.structure.commands

import com.seanshubin.code.structure.runtime.Environment
import java.nio.charset.StandardCharsets
import java.nio.file.Path

data class CreateFileCommand(
    override val source: String,
    val path: Path,
    val lines: List<String>
) : Command {
    override val category: String get() = "CreateFileCommand"
    override val id: String get() = "CreateFileCommand(path = $path, line count = ${lines.size})"
    override fun execute(environment: Environment) {
        environment.files.createDirectories(path.parent)
        environment.files.write(path, lines, StandardCharsets.UTF_8)
    }
}
