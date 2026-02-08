package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.composition.ArgsDependencies
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
        val integrations: Integrations = ProductionIntegrations
        val argsDependencies = ArgsDependencies(args, integrations)
        argsDependencies.runner.run()
        return if (argsDependencies.errorMessageHolder.errorMessage == null) 0 else 1
    }
}
