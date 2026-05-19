package com.seanshubin.code.structure.composition

class Bootstrap(
    private val integrations: Integrations
) {
    fun loadConfiguration(): Configuration {
        val commandLineArgs = integrations.commandLineArgs
        val configBaseName = commandLineArgs.firstOrNull() ?: "code-structure"
        val loader = ConfigurationLoader(integrations, configBaseName)
        return loader.load()
    }

    fun loadConfiguration(configBaseName: String): Configuration {
        val loader = ConfigurationLoader(integrations, configBaseName)
        return loader.load()
    }
}
