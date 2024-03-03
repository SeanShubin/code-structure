package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.injection.Dependencies
import com.seanshubin.code.structure.injection.Integrations
import java.time.Clock
import kotlin.system.exitProcess

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        val configBaseName = if (args.isEmpty() || args[0].isBlank()) {
            "code-structure"
        } else {
            args[0]
        }
        val integrations = object:Integrations{
            override val clock: Clock = Clock.systemUTC()
            override val emitLine: (String) -> Unit = ::println
            override val configBaseName: String = configBaseName
        }
        val dependencies = Dependencies(integrations)
        dependencies.runner.run()
        val exitCode = if(dependencies.errorMessageHolder.errorMessage == null) 0 else 1
        exitProcess(exitCode)
    }
}
