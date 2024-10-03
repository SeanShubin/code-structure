package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.durationformat.DurationFormat
import java.nio.file.Path
import java.time.Duration

class NotificationsImpl(private val emitLine: (String) -> Unit) : Notifications {
    override fun configFileEvent(configFile: Path) {
        emitLine(configFile.toString())
    }

    override fun timeTakenEvent(caption: String, timeTaken: Duration) {
//        val formattedTime = DurationFormat.milliseconds.format(timeTaken.toMillis())
//        emitLine("$formattedTime: $caption")
    }

    override fun fullAppTimeTakenEvent(timeTaken: Duration) {
        val formattedTime = DurationFormat.milliseconds.format(timeTaken.toMillis())
        emitLine("Took $formattedTime")
    }

    override fun errorReportEvent(lines: List<String>) {
        lines.forEach(emitLine)
    }

    override fun summaryEvent(summary: Summary) {
        ErrorType.entries.forEach { errorType ->
            val value = summary.errors.getValue(errorType)
            val count = value.count
            val name = errorType.caption
            if(value.isPartOfTotal){
                emitLine("$name: $count")
            } else {
                emitLine("$name: $count (not part of total)")
            }
        }
        emitLine("total: ${summary.errorCount} of ${summary.errorLimit} errors allowed")
    }
}
