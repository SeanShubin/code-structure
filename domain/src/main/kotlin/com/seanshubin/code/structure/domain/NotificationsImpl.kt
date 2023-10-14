package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.durationformat.DurationFormat
import java.time.Duration

class NotificationsImpl(private val emitLine: (String) -> Unit) : Notifications {
    override fun configFileEvent(configFileName: String) {
        emitLine(configFileName)
    }

    override fun timeTakenEvent(timeTaken: Duration) {
        val formattedTime = DurationFormat.milliseconds.format(timeTaken.toMillis())
        emitLine("Took $formattedTime")
    }
}