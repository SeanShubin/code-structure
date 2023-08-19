package com.seanshubin.code.structure.domain

import java.time.Duration

interface ReportGenerator {
    fun generateReports(analysis: Analysis)
    fun generateIndex(duration: Duration)
}
