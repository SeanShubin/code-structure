package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.filefinder.FileFinder
import java.nio.file.Path
import java.time.Clock
import java.time.Duration

class Runner(
    private val clock: Clock,
    private val inputDir: Path,
    private val isSourceFile:(Path)->Boolean,
    private val fileFinder: FileFinder,
    private val reportGenerator:ReportGenerator

) : Runnable {
    override fun run() {
        val startTime = clock.instant()
        val sourceFiles = fileFinder.findFiles(inputDir, isSourceFile)
        reportGenerator.sourceFiles(sourceFiles)
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        reportGenerator.index(duration)
    }
}
