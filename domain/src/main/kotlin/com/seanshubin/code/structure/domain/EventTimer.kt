package com.seanshubin.code.structure.domain

import java.time.Clock
import java.time.Duration

class EventTimer(
    private val timeTakenEvent: (String, Duration) -> Unit,
    private val clock: Clock
) : Timer {
    override fun <T> monitor(caption: String, f: () -> T): T {
        val startTime = clock.instant()
        val result = f()
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        timeTakenEvent(caption, duration)
        return result
    }
}
