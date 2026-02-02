package com.seanshubin.code.structure.commands

import com.seanshubin.code.structure.appconfig.Environment
import com.seanshubin.code.structure.events.Timer

class CommandRunnerImpl(
    private val timer: Timer,
    private val environment: Environment
) : CommandRunner {
    override fun execute(command: Command) {
        timer.monitor(command.source, command.category, command.id) {
            command.execute(environment)
        }
    }
}
