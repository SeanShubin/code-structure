package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.html.BigListClassName
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil
import java.nio.file.Path

class EntryPointsReport : Report {
    override val reportName: String = "entry-points"
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
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
            HtmlElementUtil.bigList(entryPoints, ::nameToElement, BigListClassName.COLUMN_1, "entry point")
        return listElements
    }

    private fun nameToElement(name: String): List<HtmlElement> =
        listOf(ReportUtil.toLocalLink(name))
}
