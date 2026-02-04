package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.filefinder.FilterStats
import com.seanshubin.code.structure.html.HtmlElementUtil
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class FilterStatisticsByPatternReport(
    private val filterStats: FilterStats,
    private val categoryName: String
) : Report {
    override val reportName: String = "filter-statistics-$categoryName-by-pattern"
    override val category: ReportCategory = ReportCategory.BROWSE

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val matchedEvents = filterStats.getMatchedEvents(categoryName)

        val patternToFiles = matchedEvents
            .groupBy { "${it.type}: ${it.pattern}" }
            .mapValues { (_, events) ->
                events.map { it.file.toString() }.distinct().sorted()
            }
            .toSortedMap()

        val tableRows = patternToFiles.flatMap { (pattern, files) ->
            files.mapIndexed { index, file ->
                if (index == 0) {
                    PatternFileRow(pattern, file)
                } else {
                    PatternFileRow("", file)
                }
            }
        }

        val captions = listOf("Pattern", "File")

        val htmlInsideBody = HtmlElementUtil.createTableWithText(
            list = tableRows,
            captions = captions,
            elementToRow = { row ->
                listOf(
                    row.pattern,
                    row.file
                )
            },
            caption = "Files Grouped by Pattern ($categoryName)"
        )

        val html = ReportHelper.wrapInTopLevelHtml(
            "By Pattern: $categoryName",
            htmlInsideBody,
            listOf(Page.tableOfContents, Page.filterStatistics)
        )
        val path = reportDir.resolve("filter-statistics-$categoryName-by-pattern.html")
        val lines = html.toLines()

        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private data class PatternFileRow(val pattern: String, val file: String)
}
