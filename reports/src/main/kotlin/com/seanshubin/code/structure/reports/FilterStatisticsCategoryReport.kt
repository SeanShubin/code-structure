package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.filefinder.FilterStats
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import com.seanshubin.code.structure.html.HtmlUtil
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class FilterStatisticsCategoryReport(
    private val filterStats: FilterStats,
    private val categoryName: String
) : Report {
    override val reportName: String = "filter-statistics-$categoryName"
    override val category: ReportCategory = ReportCategory.BROWSE

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)

        // Calculate counts for each section
        val matchedEvents = filterStats.getMatchedEvents(categoryName)
        val unmatchedEvents = filterStats.getUnmatchedEvents(categoryName)
        val unusedIncludePatterns = filterStats.getUnusedIncludePatterns(categoryName)
        val unusedExcludePatterns = filterStats.getUnusedExcludePatterns(categoryName)

        val uniqueFilesCount = matchedEvents.map { it.file }.distinct().size
        val uniquePatternsCount = matchedEvents.map { "${it.type}: ${it.pattern}" }.distinct().size
        val unmatchedFilesCount = unmatchedEvents.size
        val unusedPatternsCount = unusedIncludePatterns.size + unusedExcludePatterns.size
        val multiPatternFilesCount = matchedEvents
            .groupBy { it.file }
            .count { (_, events) -> events.map { "${it.type}: ${it.pattern}" }.distinct().size > 1 }

        val sections = listOf(
            "by-file" to "Files Sorted by Name" to uniqueFilesCount,
            "by-pattern" to "Files Grouped by Pattern" to uniquePatternsCount,
            "unmatched-files" to "Files Not Matched by Any Pattern" to unmatchedFilesCount,
            "unused-patterns" to "Patterns That Never Matched" to unusedPatternsCount,
            "multi-pattern-files" to "Files Matched by Multiple Patterns" to multiPatternFilesCount
        )

        val htmlInsideBody = generateHtml(sections)
        val html = ReportHelper.wrapInTopLevelHtml(
            "Filter Statistics: $categoryName",
            htmlInsideBody,
            listOf(Page.tableOfContents, Page.filterStatistics)
        )
        val path = reportDir.resolve("filter-statistics-$categoryName.html")
        val lines = html.toLines()

        return listOf(CreateFileCommand(reportName, path, lines))
    }

    private fun generateHtml(sections: List<Pair<Pair<String, String>, Int>>): List<HtmlElement> {
        val links = sections.map { (section, count) ->
            val (sectionId, sectionTitle) = section
            val titleWithCount = if (count == 0) sectionTitle else "$sectionTitle ($count)"
            Tag(
                "div", listOf(
                    HtmlUtil.anchor(titleWithCount, "filter-statistics-$categoryName-$sectionId.html")
                )
            )
        }

        return listOf(
            Tag("h2", listOf(Text(listOf("Sections for $categoryName")))),
            Tag("div", links, listOf("class" to "column-1"))
        )
    }
}
