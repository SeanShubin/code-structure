package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil
import java.nio.file.Path

class EntryPointsReport : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val name = Pages.entryPoints.name
        val path = Pages.entryPoints.reportFilePath(reportDir)
        val parents = listOf(Pages.tableOfContents)
        val content = createContent(validated.analysis.global)
        val lines = ReportHelper.wrapInTopLevelHtml(name, content, parents).toLines()
        val createFile = CreateFileCommand(path, lines)
        return listOf(createFile)
    }

    private fun createContent(analysis: ScopedAnalysis): List<HtmlElement> {
        val entryPoints = analysis.entryPoints
        val listElements = HtmlElementUtil.bigList(entryPoints, ::nameToElement,"big-list", "entry point")
        return listElements
    }

    private fun nameToElement(name: String): List<HtmlElement> =
        listOf(HtmlElementUtil.anchor(name, "local-$name.html"))
}
