package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.filefinder.FilterStats
import com.seanshubin.code.structure.html.HtmlUtil
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class FilterStatisticsUnusedPatternsReport(
    private val filterStats: FilterStats,
    private val categoryName: String
) : Report {
    override val reportName: String = "filter-statistics-$categoryName-unused-patterns"
    override val category: ReportCategory = ReportCategory.BROWSE

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)

        val unusedIncludePatterns = filterStats.getUnusedIncludePatterns(categoryName)
            .map { PatternRow("include", it) }
        val unusedExcludePatterns = filterStats.getUnusedExcludePatterns(categoryName)
            .map { PatternRow("exclude", it) }

        val tableRows = (unusedIncludePatterns + unusedExcludePatterns).sortedBy { it.pattern }

        val captions = listOf("Type", "Pattern")

        val htmlInsideBody = HtmlUtil.createTableWithText(
            list = tableRows,
            captions = captions,
            elementToRow = { row ->
                listOf(
                    row.type,
                    row.pattern
                )
            },
            caption = "Unused Patterns ($categoryName)"
        )

        val html = ReportHelper.wrapInTopLevelHtml(
            "Unused Patterns: $categoryName",
            htmlInsideBody,
            listOf(Page.tableOfContents, Page.filterStatistics)
        )
        val path = reportDir.resolve("filter-statistics-$categoryName-unused-patterns.html")
        val lines = html.toLines()

        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private data class PatternRow(val type: String, val pattern: String)
}
