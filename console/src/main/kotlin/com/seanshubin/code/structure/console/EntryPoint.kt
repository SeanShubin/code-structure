package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.composition.ApplicationDependencies
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

        val bootstrapDeps = BootstrapDependencies(integrations)
        val configuration = bootstrapDeps.bootstrap.loadConfiguration()

        val appDeps = ApplicationDependencies(integrations, configuration)
        appDeps.runner.run()

        return if (appDeps.errorMessageHolder.errorMessage == null) 0 else 1
    }
}
