package com.seanshubin.code.structure.injection

import com.seanshubin.code.structure.domain.ErrorMessageHolder

class ArgsDependencies(
    args: Array<String>,
    integrations: Integrations
) {
    private val configBaseName = args.firstOrNull()?.takeIf { it.isNotBlank() } ?: "code-structure"
    private val configDependencies = ConfigDependencies(configBaseName, integrations)
    val runner: Runnable = configDependencies.runner
    val errorMessageHolder: ErrorMessageHolder = configDependencies.errorMessageHolder
}
