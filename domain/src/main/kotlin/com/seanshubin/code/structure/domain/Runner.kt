package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.durationformat.DurationFormat
import java.nio.file.Path
import java.time.Clock
import java.time.Duration

class Runner(
    private val clock: Clock,
    private val observer: Observer,
    private val analyzer: Analyzer,
    private val reportGenerator: ReportGenerator,
    private val commandRunner: CommandRunner,
    private val timeTakenEvent: (Duration) -> Unit,
    private val configFile: Path,
    private val configFileEvent: (Path) -> Unit,
    private val errorEvent: (ErrorDetail) -> Unit,
    private val exit: (Int) -> Unit
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
        if (analysis.errorDetail == null) {
            exit(0)
        } else {
            errorEvent(analysis.errorDetail)
            exit(1)
        }
    }
}
