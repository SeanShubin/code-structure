package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.filefinder.FilterStats
import com.seanshubin.code.structure.html.HtmlElementUtil
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class FilterStatisticsMultiPatternFilesReport(
    private val filterStats: FilterStats,
    private val categoryName: String
) : Report {
    override val reportName: String = "filter-statistics-$categoryName-multi-pattern-files"
    override val category: ReportCategory = ReportCategory.BROWSE

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val matchedEvents = filterStats.getMatchedEvents(categoryName)

        val fileToPatterns = matchedEvents
            .groupBy { it.file }
            .mapValues { (_, events) ->
                events.map { "${it.type}: ${it.pattern}" }.distinct().sorted()
            }
            .filter { (_, patterns) -> patterns.size > 1 }
            .toSortedMap(compareBy { it.toString() })

        val tableRows = fileToPatterns.flatMap { (file, patterns) ->
            patterns.mapIndexed { index, pattern ->
                if (index == 0) {
                    FilePatternRow(file.toString(), pattern)
                } else {
                    FilePatternRow("", pattern)
                }
            }
        }

        val captions = listOf("File", "Pattern")

        val htmlInsideBody = HtmlElementUtil.createTableWithText(
            list = tableRows,
            captions = captions,
            elementToRow = { row ->
                listOf(
                    row.file,
                    row.pattern
                )
            },
            caption = "Files Matched by Multiple Patterns ($categoryName)"
        )

        val html = ReportHelper.wrapInTopLevelHtml(
            "Multi-Pattern Files: $categoryName",
            htmlInsideBody,
            listOf(Page.tableOfContents, Page.filterStatistics)
        )
        val path = reportDir.resolve("filter-statistics-$categoryName-multi-pattern-files.html")
        val lines = html.toLines()

        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private data class FilePatternRow(val file: String, val pattern: String)
}
