package com.seanshubin.code.structure.maven

import com.seanshubin.code.structure.contract.delegate.FilesDelegate
import com.seanshubin.code.structure.exec.ExecImpl
import com.seanshubin.code.structure.injection.Bootstrap
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
            override val clock = Clock.systemUTC()
            override val emitLine: (String) -> Unit = log::info
            override val files = FilesDelegate
            override val exec = ExecImpl()
        }
        val bootstrap = Bootstrap(integrations, nonNullConfigBaseName)
        val configuration = bootstrap.loadConfiguration()
        val dependencies = Dependencies(integrations, configuration)
        dependencies.runner.run()
        val errorMessage = dependencies.errorMessageHolder.errorMessage
        if (errorMessage != null) {
            throw MojoFailureException(errorMessage)
        }
    }
}
