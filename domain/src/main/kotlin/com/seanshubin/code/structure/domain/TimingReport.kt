package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.durationformat.DurationFormat
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil
import java.nio.file.Path
import java.time.Duration

class TimingReport(private val timer: Timer) : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val content = createContent()
        val root = ReportHelper.wrapInTopLevelHtml(Pages.timing.name, content, listOf(Pages.tableOfContents))
        val path = reportDir.resolve(Pages.timing.fileName)
        val lines = root.toLines()
        val createFileCommand = CreateFileCommand(path, lines)
        return listOf(createFileCommand)
    }

    private fun createContent(): List<HtmlElement> {
        val events = timer.events()
        val summaries = timer.summaries().sortedByDescending { it.total }
        val eventTable = HtmlElementUtil.createTable(events, timingEventCaptions, ::timingEventToRow, "event")
        val summaryTable = HtmlElementUtil.createTable(summaries, timingSummaryCaptions, ::timingSummaryToRow, caption = null)
        return summaryTable + eventTable
    }

    companion object {
        private val timingEventCaptions = listOf("category", "duration", "caption")
        private fun timingEventToRow(timingEvent: TimingEvent): List<String> {
            return listOf(
                timingEvent.category,
                timingEvent.duration.formatMilliseconds(),
                timingEvent.caption
            )
        }

        private val timingSummaryCaptions = listOf("category", "count", "total", "median", "min", "max")
        private fun timingSummaryToRow(timingSummary: TimingSummary): List<String> {
            val medianString = if (timingSummary.median.size == 1) {
                timingSummary.median[0].formatMilliseconds()
            } else if (timingSummary.median[0].toMillis() == timingSummary.median[1].toMillis()) {
                timingSummary.median[0].formatMilliseconds()
            } else {
                timingSummary.median.joinToString(", ", "[", "]") {
                    it.formatMilliseconds()
                }
            }
            return listOf(
                timingSummary.category,
                timingSummary.count.toString(),
                timingSummary.total.formatMilliseconds(),
                medianString,
                timingSummary.minimum.formatMilliseconds(),
                timingSummary.maximum.formatMilliseconds()
            )
        }

        private fun Duration.formatMilliseconds(): String =
            DurationFormat.milliseconds.format(toMillis())
    }
}
