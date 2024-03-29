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
        emitLine("in direct cycle: ${summary.inCycleCount}")
        emitLine("in group cycle: ${summary.inGroupCycleCount}")
        emitLine("ancestor depends on descendant: ${summary.ancestorDependsOnDescendantCount}")
        emitLine("descendant depends on ancestor: ${summary.descendantDependsOnAncestorCount}")
    }

    override fun cycleLoopEvent(caption: String, index: Int, size: Int) {
//        emitLine("cycle loop $caption ${index+1} of $size")
    }
}
