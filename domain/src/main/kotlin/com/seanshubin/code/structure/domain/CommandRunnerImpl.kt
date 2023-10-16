package com.seanshubin.code.structure.domain

class CommandRunnerImpl(
    private val timer:Timer,
    private val environment: Environment
) : CommandRunner {
    override fun execute(command: Command) {
        timer.monitor(command.id){
            command.execute(environment)
        }
    }
}
