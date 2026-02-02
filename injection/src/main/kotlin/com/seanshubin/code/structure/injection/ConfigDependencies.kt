package com.seanshubin.code.structure.injection

import com.seanshubin.code.structure.domain.ErrorMessageHolder

class ConfigDependencies(
    configBaseName: String,
    integrations: Integrations
) {
    private val bootstrap = Bootstrap(integrations, configBaseName)
    private val configuration = bootstrap.loadConfiguration()
    private val dependencies = Dependencies(integrations, configuration)
    val runner: Runnable = dependencies.runner
    val errorMessageHolder: ErrorMessageHolder = dependencies.errorMessageHolder
}
