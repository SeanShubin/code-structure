package com.seanshubin.code.structure.injection

class Bootstrap(
    private val integrations: Integrations,
    private val configBaseName: String
) {
    private val configurationLoader = ConfigurationLoader(integrations, configBaseName)
    val configuration: Configuration = configurationLoader.load()
}
