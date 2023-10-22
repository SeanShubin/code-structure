package com.seanshubin.code.structure.domain

import java.nio.file.Path
import java.time.Clock
import java.time.Duration

class Runner(
    private val clock: Clock,
    private val observer: Observer,
    private val analyzer: Analyzer,
    private val validator: Validator,
    private val reportGenerator: ReportGenerator,
    private val commandRunner: CommandRunner,
    private val timeTakenEvent: (Duration) -> Unit,
    private val configFile: Path,
    private val configFileEvent: (Path) -> Unit,
    private val timer: Timer,
    private val exitCodeHolder: ExitCodeHolder,
    private val errorHandler: ErrorHandler
) : Runnable {
    override fun run() {
        configFileEvent(configFile)
        val startTime = clock.instant()
        val observations = timer.monitor("observations") { observer.makeObservations() }
        val analysis = timer.monitor("analysis") { analyzer.analyze(observations) }
        val validated = timer.monitor("validation") { validator.validate(observations, analysis) }
        val commands = timer.monitor("reports") { reportGenerator.generateReports(validated) }
        timer.monitor("commands") { commands.forEach { commandRunner.execute(it) } }
        val finalCommands = reportGenerator.generateFinalReports(validated)
        finalCommands.forEach { commandRunner.execute(it) }
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        timeTakenEvent(duration)
        val exitCode = errorHandler.handleErrors(validated.observations.configuredErrors, validated.analysis.errors)
        exitCodeHolder.exitCode = exitCode
    }
}
