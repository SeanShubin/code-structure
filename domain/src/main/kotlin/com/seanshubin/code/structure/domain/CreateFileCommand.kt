package com.seanshubin.code.structure.domain

import java.nio.charset.StandardCharsets
import java.nio.file.Path

data class CreateFileCommand(val path: Path, val lines: List<String>) : Command {
    override fun execute(environment: Environment) {
        environment.files.createDirectories(path.parent)
        environment.files.write(path, lines, StandardCharsets.UTF_8)
    }
}
