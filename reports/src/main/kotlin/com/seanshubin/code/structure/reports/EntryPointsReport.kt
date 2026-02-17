package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlUtil.anchor
import com.seanshubin.code.structure.reports.ReportHtmlUtil.bigList
import com.seanshubin.code.structure.model.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.model.ScopedAnalysis
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class EntryPointsReport : Report {
    override val reportName: String = "entry-points"
    override val category: ReportCategory = ReportCategory.BROWSE
    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)
        val path = reportDir.resolve(Page.entryPoints.file)
        val parents = listOf(Page.tableOfContents)
        val content = createContent(validated.analysis.global)
        val lines = ReportHelper.wrapInTopLevelHtml(Page.entryPoints.caption, content, parents).toLines()
        val createFile = CreateFileCommand(reportName, path, lines)
        return listOf(createFile)
    }

    private fun createContent(analysis: ScopedAnalysis): List<HtmlElement> {
        val entryPoints = analysis.entryPoints
        val listElements =
            bigList(entryPoints, ::nameToElement, BigListClassName.COLUMN_1, "entry point")
        return listElements
    }

    private fun nameToElement(name: String): List<HtmlElement> =
        listOf(anchor(name, name.toCodeUnit().parent().toUriName("group", ".html")))
}
