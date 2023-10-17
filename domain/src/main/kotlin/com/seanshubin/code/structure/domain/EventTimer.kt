package com.seanshubin.code.structure.domain

import java.time.Clock
import java.time.Duration

class EventTimer(
    private val timeTakenEvent: (String, Duration) -> Unit,
    private val clock: Clock
) : Timer {
    private val events = mutableListOf<TimingEvent>()
    override fun <T> monitor(category: String, f: () -> T): T =
        monitor(category, category, f)

    override fun <T> monitor(category: String, caption: String, f: () -> T): T {
        val startTime = clock.instant()
        val result = f()
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        events.add(TimingEvent(category, caption, duration))
        timeTakenEvent(caption, duration)
        return result
    }

    override fun events(): List<TimingEvent> {
        return events
    }

    override fun summaries(): List<TimingSummary> {
        val eventsByCategory = events.groupBy { it.category }
        val categories = eventsByCategory.keys
        val details = categories.map { category ->
            val eventsForCategory = eventsByCategory.getValue(category)
            val durations = eventsForCategory.map { it.duration }
            val count = durations.size
            val maxDuration = durations.max()
            val minDuration = durations.min()
            val medianDuration = durations.median()
            val totalDuration = durations.fold(Duration.ZERO) { x, y -> x + y }
            TimingSummary(category, count, minDuration, maxDuration, medianDuration, totalDuration)
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
