package com.seanshubin.code.structure.domain

interface ReportGenerator {
    fun generateReports(validated: Validated): List<Command>
    fun generateFinalReports(validated: Validated): List<Command>
}
