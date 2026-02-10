package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.composition.BootstrapDependencies
import com.seanshubin.code.structure.composition.Integrations
import com.seanshubin.code.structure.composition.ProductionIntegrations
import kotlin.system.exitProcess

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        val exitCode = execute(args)
        exitProcess(exitCode)
    }

    fun execute(args: Array<String>): Int {
        val integrations: Integrations = ProductionIntegrations(args)
        val bootstrap = BootstrapDependencies(integrations)
        bootstrap.runner.run()
        return if (bootstrap.errorMessageHolder.errorMessage == null) 0 else 1
    }
}
