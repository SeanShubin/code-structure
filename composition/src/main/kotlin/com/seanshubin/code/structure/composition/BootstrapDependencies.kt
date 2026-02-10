package com.seanshubin.code.structure.composition

import com.seanshubin.code.structure.runtime.ErrorMessageHolder

class BootstrapDependencies(
    integrations: Integrations
) {
    private val args = integrations.commandLineArgs
    private val argsParser = ArgsParser
    private val configBaseName = argsParser.parseConfigBaseName(args)
    private val bootstrap = Bootstrap(integrations, configBaseName)
    private val configuration = bootstrap.configuration
    private val applicationDependencies = ApplicationDependencies(integrations, configuration)
    val runner: Runnable = applicationDependencies.runner
    val errorMessageHolder: ErrorMessageHolder = applicationDependencies.errorMessageHolder
}
