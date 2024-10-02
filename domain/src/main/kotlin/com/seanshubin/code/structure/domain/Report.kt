package com.seanshubin.code.structure.domain

import java.nio.file.Path

interface Report {
    val reportName: String
    fun generate(reportDir: Path, validated: Validated): List<Command>
}
