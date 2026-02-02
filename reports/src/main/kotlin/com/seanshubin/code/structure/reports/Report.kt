package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

interface Report {
    val reportName: String
    val category: ReportCategory
    fun generate(baseReportDir: Path, validated: Validated): List<Command>
}
