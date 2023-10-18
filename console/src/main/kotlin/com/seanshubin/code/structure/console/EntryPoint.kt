package com.seanshubin.code.structure.console

import kotlin.system.exitProcess

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        val integrations = ProductionIntegrations()
        val dependencies = Dependencies(integrations, args)
        dependencies.runner.run()
        exitProcess(dependencies.exitCodeHolder.exitCode)
    }
}
