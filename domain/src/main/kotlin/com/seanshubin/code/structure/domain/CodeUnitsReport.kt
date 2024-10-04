package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.html.BigListClassName
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class CodeUnitsReport : Report {
    override val reportName: String = "code-units"

    override fun generate(reportDir: Path, validated: Validated): List<Command> {
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
