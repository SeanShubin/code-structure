package com.seanshubin.code.structure.composition

import com.seanshubin.code.structure.runtime.ErrorMessageHolder

class ArgsDependencies(
    args: Array<String>,
    integrations: Integrations
) {
    private val argsParser = ArgsParser
    private val configBaseName = argsParser.parseConfigBaseName(args)
    private val configDependencies = ConfigDependencies(configBaseName, integrations)
    val runner: Runnable = configDependencies.runner
    val errorMessageHolder: ErrorMessageHolder = configDependencies.errorMessageHolder
}
