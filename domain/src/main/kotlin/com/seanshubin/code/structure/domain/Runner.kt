package com.seanshubin.code.structure.domain

import java.time.Clock
import java.time.Duration

class Runner(
    private val clock: Clock,
    private val observer: Observer,
    private val analyzer: Analyzer,
    private val reportGenerator: ReportGenerator
) : Runnable {
    override fun run() {
        val startTime = clock.instant()
        val observations = observer.makeObservations()
        val analysis = analyzer.analyze(observations)
        reportGenerator.generateReports(analysis)
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        reportGenerator.generateIndex(duration)
    }
}
