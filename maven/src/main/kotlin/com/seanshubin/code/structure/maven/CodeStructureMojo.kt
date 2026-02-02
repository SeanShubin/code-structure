package com.seanshubin.code.structure.maven

import com.seanshubin.code.structure.injection.ConfigDependencies
import com.seanshubin.code.structure.injection.Integrations
import com.seanshubin.code.structure.injection.ProductionIntegrations
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter

@Mojo(name = "analyze", defaultPhase = LifecyclePhase.VERIFY, aggregator = true)
class CodeStructureMojo : AbstractMojo() {
    @Parameter(property = "scope")
    var configBaseName: String? = null
    override fun execute() {
        val nonNullConfigBaseName = configBaseName ?: "code-structure"
        val integrations = object : Integrations by ProductionIntegrations {
            override val emitLine: (String) -> Unit = log::info
        }
        val configDependencies = ConfigDependencies(nonNullConfigBaseName, integrations)
        configDependencies.runner.run()
        val errorMessage = configDependencies.errorMessageHolder.errorMessage
        if (errorMessage != null) {
            throw MojoFailureException(errorMessage)
        }
    }
}
