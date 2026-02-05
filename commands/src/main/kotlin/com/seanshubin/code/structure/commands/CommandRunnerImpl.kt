package com.seanshubin.code.structure.commands

import com.seanshubin.code.structure.runtime.Environment

class CommandRunnerImpl(
    private val environment: Environment
) : CommandRunner {
    override fun execute(command: Command) {
        command.execute(environment)
    }
}
