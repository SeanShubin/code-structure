package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.contract.delegate.FilesDelegate
import com.seanshubin.code.structure.exec.ExecImpl
import com.seanshubin.code.structure.injection.Bootstrap
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
        val integrations = object : Integrations {
            override val clock = Clock.systemUTC()
            override val emitLine: (String) -> Unit = { println(it) }
            override val files = FilesDelegate
            override val exec = ExecImpl()
        }
        val bootstrap = Bootstrap(integrations, configBaseName)
        val configuration = bootstrap.loadConfiguration()
        val dependencies = Dependencies(integrations, configuration)
        dependencies.runner.run()
        val exitCode = if (dependencies.errorMessageHolder.errorMessage == null) 0 else 1
        exitProcess(exitCode)
    }
}
