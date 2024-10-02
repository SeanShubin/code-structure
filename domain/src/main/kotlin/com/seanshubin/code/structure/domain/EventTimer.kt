package com.seanshubin.code.structure.domain

import java.time.Clock
import java.time.Duration

class EventTimer(
    private val timeTakenEvent: (String, Duration) -> Unit,
    private val clock: Clock
) : Timer {
    private val events = mutableListOf<TimingEvent>()
    override fun <T> monitor(source:String, category: String, f: () -> T): T =
        monitor(source, category, category, f)

    override fun <T> monitor(source:String, category: String, caption: String, f: () -> T): T {
        val startTime = clock.instant()
        val result = f()
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        events.add(TimingEvent(source, category, caption, duration))
        timeTakenEvent(caption, duration)
        return result
    }

    override fun events(): List<TimingEvent> {
        return events
    }

    override fun summaries(): List<TimingSummary> {
        val eventsByCategory = events.groupBy { it.key }
        val categories = eventsByCategory.keys
        val details = categories.map { key ->
            val eventsForCategory = eventsByCategory.getValue(key)
            val durations = eventsForCategory.map { it.duration }
            val count = durations.size
            val maxDuration = durations.max()
            val minDuration = durations.min()
            val medianDuration = durations.median()
            val totalDuration = durations.fold(Duration.ZERO) { x, y -> x + y }
            TimingSummary(key, count, minDuration, maxDuration, medianDuration, totalDuration)
        }
        return details
    }

    companion object {
        private fun List<Duration>.median(): List<Duration> =
            if (size % 2 == 0) {
                listOf(get(size / 2 - 1), get(size / 2))
            } else {
                listOf(get(size / 2))
            }
    }
}
