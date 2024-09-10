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
    private val summaryEvent: (Summary) -> Unit,
    private val timer: Timer,
    private val exitCodeHolder: ErrorMessageHolder,
    private val errorHandler: ErrorHandler,
    private val countAsErrors: CountAsErrors,
) : Runnable {
    override fun run() {
        configFileEvent(configFile)
        val startTime = clock.instant()
        val validated = timer.monitor("all before final report"){
            val observations = timer.monitor("observations") { observer.makeObservations() }
            val analysis = timer.monitor("analysis") { analyzer.analyze(observations) }
            val validated = timer.monitor("validation") { validator.validate(observations, analysis) }
            val commands = timer.monitor("reports") { reportGenerator.generateReports(validated) }
            timer.monitor("commands") { commands.forEach { commandRunner.execute(it) } }
            validated
        }
        // final commands create the report on timing, so no point in monitoring time after this point
        val finalCommands = reportGenerator.generateFinalReports(validated)
        finalCommands.forEach { commandRunner.execute(it) }
        val errorMessage = errorHandler.handleErrors(
            validated.observations.configuredErrors,
            validated.analysis.errors,
            countAsErrors
        )
        exitCodeHolder.errorMessage = errorMessage
        summaryEvent(validated.analysis.summary)
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        timeTakenEvent(duration)
    }
}
