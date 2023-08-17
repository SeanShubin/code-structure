package com.seanshubin.code.structure.domain

import java.nio.file.Path
import java.time.Duration

interface ReportGenerator {
    fun sourceFiles(sourceFiles:List<Path>)
    fun index(duration: Duration)
}
