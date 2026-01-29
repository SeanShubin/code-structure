package com.seanshubin.code.structure.maven

import com.seanshubin.code.structure.injection.Dependencies
import com.seanshubin.code.structure.injection.Integrations
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.time.Clock

@Mojo(name = "analyze", defaultPhase = LifecyclePhase.VERIFY, aggregator = true)
class CodeStructureMojo : AbstractMojo() {
    @Parameter(property = "scope")
    var configBaseName: String? = null
    override fun execute() {
        val nonNullConfigBaseName = configBaseName ?: "code-structure"
        val integrations = object : Integrations {
            override val clock: Clock = Clock.systemUTC()
            override val emitLine: (String) -> Unit = log::info
            override val configBaseName: String = nonNullConfigBaseName
        }
        val dependencies = Dependencies(integrations)
        dependencies.runner.run()
        val errorMessage = dependencies.errorMessageHolder.errorMessage
        if (errorMessage != null) {
            throw MojoFailureException(errorMessage)
        }
    }
}
