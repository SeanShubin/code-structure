package com.seanshubin.code.structure.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class CodeStructurePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("codeStructure", CodeStructureExtension::class.java)

        project.tasks.register("analyzeCodeStructure", CodeStructureTask::class.java) { task ->
            task.group = "verification"
            task.description = "Analyze code structure and generate dependency reports"
            task.configFile.set(extension.configFile)
        }
    }
}
