package com.seanshubin.code.structure.domain

import java.nio.file.Path
import java.time.Duration

class ReportGeneratorImpl:ReportGenerator {
    override fun sourceFiles(sourceFiles: List<Path>) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun index(duration: Duration) {
        throw UnsupportedOperationException("not implemented")
    }
}
