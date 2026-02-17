package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.filefinder.FilterStats
import com.seanshubin.code.structure.html.HtmlUtil
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class FilterStatisticsUnmatchedFilesReport(
    private val filterStats: FilterStats,
    private val categoryName: String
) : Report {
    override val reportName: String = "filter-statistics-$categoryName-unmatched-files"
    override val category: ReportCategory = ReportCategory.BROWSE

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val unmatchedEvents = filterStats.getUnmatchedEvents(categoryName)

        val files = unmatchedEvents
            .map { it.file.toString() }
            .distinct()
            .sorted()

        val tableRows = files.map { file ->
            UnmatchedFileRow(file)
        }

        val captions = listOf("File")

        val htmlInsideBody = HtmlUtil.createTableWithText(
            list = tableRows,
            captions = captions,
            elementToRow = { row ->
                listOf(
                    row.file
                )
            },
            caption = "Files Not Matched by Any Pattern ($categoryName)"
        )

        val html = ReportHelper.wrapInTopLevelHtml(
            "Unmatched Files: $categoryName",
            htmlInsideBody,
            listOf(Page.tableOfContents, Page.filterStatistics)
        )
        val path = reportDir.resolve("filter-statistics-$categoryName-unmatched-files.html")
        val lines = html.toLines()

        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private data class UnmatchedFileRow(val file: String)
}
