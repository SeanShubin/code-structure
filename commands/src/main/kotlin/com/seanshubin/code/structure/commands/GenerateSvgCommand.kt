package com.seanshubin.code.structure.commands

import com.seanshubin.code.structure.runtime.Environment
import java.nio.file.Path

data class GenerateSvgCommand(
    override val source: String,
    val input: Path,
    val output: Path
) : Command {
    override val category: String get() = "GenerateSvgCommand"
    override val id: String get() = "GenerateSvgCommand(input = $input, output = $output)"

    override fun execute(environment: Environment) {
        environment.exec.exec("dot", "-Tsvg", "-o$output", input.toString())
    }
}
