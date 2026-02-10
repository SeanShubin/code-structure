package com.seanshubin.code.structure.maven

import com.seanshubin.code.structure.composition.ApplicationDependencies
import com.seanshubin.code.structure.composition.BootstrapDependencies
import com.seanshubin.code.structure.composition.Integrations
import com.seanshubin.code.structure.composition.ProductionIntegrations
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
        val productionIntegrations = ProductionIntegrations(emptyArray())
        val integrations = object : Integrations by productionIntegrations {
            override val emitLine: (String) -> Unit = log::info
        }

        val bootstrapDeps = BootstrapDependencies(integrations)
        val configuration = bootstrapDeps.bootstrap.loadConfiguration(nonNullConfigBaseName)

        val appDeps = ApplicationDependencies(integrations, configuration)
        appDeps.runner.run()

        val errorMessage = appDeps.errorMessageHolder.errorMessage
        if (errorMessage != null) {
            throw MojoFailureException(errorMessage)
        }
    }
}
