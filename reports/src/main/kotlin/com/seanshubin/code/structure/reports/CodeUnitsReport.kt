package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.commands.Command
import com.seanshubin.code.structure.commands.CreateFileCommand
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlUtil.anchor
import com.seanshubin.code.structure.reports.ReportHtmlUtil.bigList
import com.seanshubin.code.structure.model.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.model.Validated
import java.nio.file.Path

class CodeUnitsReport : Report {
    override val reportName: String = "code-units"
    override val category: ReportCategory = ReportCategory.BROWSE

    override fun generate(baseReportDir: Path, validated: Validated): List<Command> {
        val reportDir = baseReportDir.resolve(category.directory)

        val parents = listOf(Page.tableOfContents)
        val path = reportDir.resolve(Page.codeUnits.file)
        val analysis = validated.analysis
        val content = createContent(analysis.global.names)
        val lines = ReportHelper.wrapInTopLevelHtml(Page.codeUnits.caption, content, parents).toLines()
        val index = CreateFileCommand(reportName, path, lines)
        val commands = listOf(index)
        return commands
    }

    private fun createContent(names: List<String>): List<HtmlElement> =
        createGroupList(names)

    private fun createGroupList(names: List<String>): List<HtmlElement> =
        bigList(names, ::singleGroupLink, BigListClassName.COLUMN_1, "names")

    private fun containingGroupLink(name: String, caption: String): List<HtmlElement> {
        val linkText = name.toCodeUnit().parent().toUriName("group", ".html")
        return listOf(anchor(caption, linkText))
    }

    private fun singleGroupLink(name: String): List<HtmlElement> =
        containingGroupLink(name, name)

}
