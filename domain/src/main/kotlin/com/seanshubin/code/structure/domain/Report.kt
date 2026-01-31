package com.seanshubin.code.structure.domain

import java.nio.file.Path

interface Report {
    val reportName: String
    val category: ReportCategory
    fun generate(baseReportDir: Path, validated: Validated): List<Command>}
