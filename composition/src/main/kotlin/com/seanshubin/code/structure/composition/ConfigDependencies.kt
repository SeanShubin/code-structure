package com.seanshubin.code.structure.composition

import com.seanshubin.code.structure.runtime.ErrorMessageHolder

class ConfigDependencies(
    configBaseName: String,
    integrations: Integrations
) {
    private val bootstrap = Bootstrap(integrations, configBaseName)
    private val configuration = bootstrap.configuration
    private val dependencies = Dependencies(integrations, configuration)
    val runner: Runnable = dependencies.runner
    val errorMessageHolder: ErrorMessageHolder = dependencies.errorMessageHolder
}
