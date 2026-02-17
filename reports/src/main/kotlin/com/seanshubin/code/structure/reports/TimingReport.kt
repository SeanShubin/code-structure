package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.durationformat.DurationFormat
import com.seanshubin.code.structure.events.Timer
import com.seanshubin.code.structure.events.TimingEvent
import com.seanshubin.code.structure.events.TimingSummary
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlUtil
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path
import java.time.Duration

class TimingReport(private val timer: Timer) : Report {
    override val reportName: String = "timing"
    override val category: ReportCategory = ReportCategory.BROWSE
    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val content = createContent()
        val root = ReportHelper.wrapInTopLevelHtml(Page.timing.caption, content, listOf(Page.tableOfContents))
        val path = reportDir.resolve(Page.timing.file)
        val lines = root.toLines()
        val createFileCommand = CreateFileCommand(reportName, path, lines)
        return listOf(createFileCommand)
    }

    private fun createContent(): List<HtmlElement> {
        val events = timer.events()
        val summaries = timer.summaries().sortedByDescending { it.total }
        val eventTable = HtmlUtil.createTableWithText(events, timingEventCaptions, ::timingEventToRow, "event")
        val summaryTable =
            HtmlUtil.createTableWithText(summaries, timingSummaryCaptions, ::timingSummaryToRow, caption = null)
        return summaryTable + eventTable
    }

    companion object {
        private val timingEventCaptions = listOf("key", "duration", "caption")
        private fun timingEventToRow(timingEvent: TimingEvent): List<String> {
            return listOf(
                timingEvent.key,
                timingEvent.duration.formatMilliseconds(),
                timingEvent.caption
            )
        }

        private val timingSummaryCaptions = listOf("key", "count", "total", "median", "min", "max")
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
                timingSummary.key,
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
