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

    override fun errorEvent(errorDetail: ErrorDetail) {
        val cycleCount = errorDetail.newCycles.size
        emitLine("new cycles: $cycleCount")
        errorDetail.newCycles.forEach(emitLine)
    }
}
