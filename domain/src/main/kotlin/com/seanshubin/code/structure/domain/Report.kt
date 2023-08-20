package com.seanshubin.code.structure.domain

import java.nio.file.Path

interface Report {
    val name: String
    fun generate(reportDir: Path, analysis: Analysis): CreateFileCommand
}
