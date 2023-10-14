package com.seanshubin.code.structure.domain

import java.time.Clock
import java.time.Duration

class Runner(
    private val clock: Clock,
    private val observer: Observer,
    private val analyzer: Analyzer,
    private val reportGenerator: ReportGenerator,
    private val commandRunner: CommandRunner,
    private val timeTakenEvent: (Duration) -> Unit,
    private val configFile: String,
    private val configFileEvent: (String) -> Unit
) : Runnable {
    override fun run() {
        configFileEvent(configFile)
        val startTime = clock.instant()
        val observations = observer.makeObservations()
        val analysis = analyzer.analyze(observations)
        val commands = reportGenerator.generateReports(analysis)
        commands.forEach { commandRunner.execute(it) }
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        timeTakenEvent(duration)
    }
}
