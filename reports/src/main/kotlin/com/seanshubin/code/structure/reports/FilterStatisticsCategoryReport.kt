package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.filefinder.FilterStats
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import com.seanshubin.code.structure.html.HtmlElementUtil
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

        val sections = listOf(
            "by-file" to "Files Sorted by Name",
            "by-pattern" to "Files Grouped by Pattern",
            "unmatched-files" to "Files Not Matched by Any Pattern",
            "unused-patterns" to "Patterns That Never Matched",
            "multi-pattern-files" to "Files Matched by Multiple Patterns"
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

    private fun generateHtml(sections: List<Pair<String, String>>): List<HtmlElement> {
        val links = sections.map { (sectionId, sectionTitle) ->
            Tag(
                "div", listOf(
                    HtmlElementUtil.anchor(sectionTitle, "filter-statistics-$categoryName-$sectionId.html")
                )
            )
        }

        return listOf(
            Tag("h2", listOf(Text(listOf("Sections for $categoryName")))),
            Tag("div", links, listOf("class" to "column-1"))
        )
    }
}
