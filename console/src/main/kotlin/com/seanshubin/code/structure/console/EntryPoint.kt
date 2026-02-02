package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.injection.ArgsDependencies
import com.seanshubin.code.structure.injection.Integrations
import com.seanshubin.code.structure.injection.ProductionIntegrations
import kotlin.system.exitProcess

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        val integrations: Integrations = ProductionIntegrations
        val argsDependencies = ArgsDependencies(args, integrations)
        argsDependencies.runner.run()
        val exitCode = if (argsDependencies.errorMessageHolder.errorMessage == null) 0 else 1
        exitProcess(exitCode)
    }
}
