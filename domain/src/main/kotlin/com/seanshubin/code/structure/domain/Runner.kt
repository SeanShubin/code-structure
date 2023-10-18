package com.seanshubin.code.structure.domain

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
    private val timer: Timer,
    private val exitCodeHolder:ExitCodeHolder
) : Runnable {
    override fun run() {
        configFileEvent(configFile)
        val startTime = clock.instant()
        val observations = timer.monitor("observations") { observer.makeObservations() }
        val analysis = timer.monitor("analysis") { analyzer.analyze(observations) }
        val commands = timer.monitor("reports") { reportGenerator.generateReports(analysis) }
        timer.monitor("commands") { commands.forEach { commandRunner.execute(it) } }
        val finalCommands = reportGenerator.generateFinalReports(analysis)
        finalCommands.forEach { commandRunner.execute(it) }
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        timeTakenEvent(duration)
        if (analysis.errorDetail != null) {
            errorEvent(analysis.errorDetail)
            exitCodeHolder.exitCode = 1
        }
    }
}
