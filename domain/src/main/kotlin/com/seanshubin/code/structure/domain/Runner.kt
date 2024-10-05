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
    private val exitCodeHolder: ErrorMessageHolder
) : Runnable {
    private val reportName: String = "runner"
    override fun run() {
        configFileEvent(configFile)
        val startTime = clock.instant()
        val validated = timer.monitor(reportName, "all before final report") {
            val observations = timer.monitor(reportName, "observations") { observer.makeObservations() }
            val analysis = timer.monitor(reportName, "analysis") { analyzer.analyze(observations) }
            val validated = timer.monitor(reportName, "validation") { validator.validate(observations, analysis) }
            val commands = timer.monitor(reportName, "reports") { reportGenerator.generateReports(validated) }
            timer.monitor(reportName, "commands") { commands.forEach { commandRunner.execute(it) } }
            validated
        }
        // final commands create the report on timing, so no point in monitoring time after this point
        val finalCommands = reportGenerator.generateFinalReports(validated)
        finalCommands.forEach { commandRunner.execute(it) }
        if (validated.analysis.summary.isOverLimit) {
            val errorCount = validated.analysis.summary.errorCount
            val errorLimit = validated.analysis.summary.errorLimit
            exitCodeHolder.errorMessage = "Error count $errorCount is over limit $errorLimit"
        }
        summaryEvent(validated.analysis.summary)
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        timeTakenEvent(duration)
    }
}
