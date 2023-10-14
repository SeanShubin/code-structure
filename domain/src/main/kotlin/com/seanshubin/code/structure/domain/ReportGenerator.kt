package com.seanshubin.code.structure.domain

interface ReportGenerator {
    fun generateReports(analysis: Analysis): List<Command>
}
