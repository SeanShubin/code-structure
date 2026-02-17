package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.filefinder.FilterStats
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Text
import com.seanshubin.code.structure.html.HtmlUtil
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class FilterStatisticsIndexReport(
    private val filterStats: FilterStats
) : Report {
    override val reportName: String = "filter-statistics-index"
    override val category: ReportCategory = ReportCategory.BROWSE

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val categories = filterStats.getAllCategories()

        val htmlInsideBody = generateHtml(categories)
        val html = ReportHelper.wrapInTopLevelHtml(
            "Filter Statistics",
            htmlInsideBody,
            listOf(Page.tableOfContents)
        )
        val path = reportDir.resolve("filter-statistics.html")
        val lines = html.toLines()

        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private fun generateHtml(categories: List<String>): List<HtmlElement> {
        val tableData = categories.map { category ->
            val matchedCount = filterStats.getMatchedEvents(category).size
            val unmatchedCount = filterStats.getUnmatchedEvents(category).size
            val totalFiles = matchedCount + unmatchedCount

            CategoryRow(category, totalFiles, matchedCount, unmatchedCount)
        }

        val captions = listOf("Category", "Total Files", "Matched", "Unmatched", "Details")

        return HtmlUtil.createTableWithElements(
            list = tableData,
            captions = captions,
            elementToRow = { row ->
                listOf(
                    Text(row.category),
                    Text(row.totalFiles.toString()),
                    Text(row.matchedCount.toString()),
                    Text(row.unmatchedCount.toString()),
                    HtmlUtil.anchor("View Details", "filter-statistics-${row.category}.html")
                )
            },
            caption = "Filter Statistics by Category"
        )
    }

    private data class CategoryRow(
        val category: String,
        val totalFiles: Int,
        val matchedCount: Int,
        val unmatchedCount: Int
    )
}
