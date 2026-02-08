package com.seanshubin.code.structure.gradle

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class CodeStructureExtension {
    @get:Input
    abstract val configFile: Property<String>

    init {
        configFile.convention("code-structure")
    }
}
