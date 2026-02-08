package com.seanshubin.code.structure.gradle

import com.seanshubin.code.structure.console.EntryPoint
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class CodeStructureTask : DefaultTask() {
    @get:Input
    abstract val configFile: Property<String>

    @TaskAction
    fun analyze() {
        val args = arrayOf(configFile.get())
        val exitCode = EntryPoint.execute(args)
        if (exitCode != 0) {
            throw GradleException("Code structure analysis failed with errors (see generated/code-structure/browse/index.html for details)")
        }
    }
}
