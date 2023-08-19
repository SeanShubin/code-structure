package com.seanshubin.code.structure.domain

import java.time.Duration

class ReportGeneratorImpl() : ReportGenerator {
    override fun generateReports(analysis: Analysis) {
        val sourceFiles = analysis.observations.sourceFiles
        sourceFiles.forEach(::println)
        println(sourceFiles.size)
        throw UnsupportedOperationException("not implemented")
    }

    override fun generateIndex(duration: Duration) {
        throw UnsupportedOperationException("not implemented")
    }
}
