package com.seanshubin.code.structure.domain

import java.nio.file.Path

data class GenerateSvgCommand(val input: Path, val output:Path) : Command {
    override fun execute(environment: Environment) {
        environment.exec.exec("dot", "-Tsvg", "-o$output", input.toString())
    }
}
