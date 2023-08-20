package com.seanshubin.code.structure.domain

class CommandRunnerImpl(
    private val environment: Environment
) : CommandRunner {
    override fun execute(command: Command) {
        command.execute(environment)
    }
}
