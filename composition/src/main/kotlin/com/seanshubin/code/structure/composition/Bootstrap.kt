package com.seanshubin.code.structure.composition

class Bootstrap(
    private val integrations: Integrations
) {
    private val argsParser = ArgsParser

    fun loadConfiguration(): Configuration {
        val configBaseName = argsParser.parseConfigBaseName(integrations.commandLineArgs)
        val loader = ConfigurationLoader(integrations, configBaseName)
        return loader.load()
    }

    fun loadConfiguration(configBaseName: String): Configuration {
        val loader = ConfigurationLoader(integrations, configBaseName)
        return loader.load()
    }
}
