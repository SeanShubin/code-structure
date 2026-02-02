package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.model.Validated

interface ReportGenerator {
    fun generateReports(validated: Validated): List<Command>
    fun generateFinalReports(validated: Validated): List<Command>
}
