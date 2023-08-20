package com.seanshubin.code.structure.domain

import java.nio.file.Path

interface Report {
    fun generate(reportDir: Path, analysis: Analysis): List<CreateFileCommand>
}
