package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil
import java.nio.file.Path

class EntryPointsReport : Report {
    override fun generate(reportDir: Path, analysis: Analysis): List<Command> {
        val name = Pages.entryPoints.name
        val path = reportDir.resolve(Pages.entryPoints.fileName)
        val parents = listOf(Pages.tableOfContents)
        val content = createContent(analysis)
        val lines = ReportHelper.wrapInTopLevelHtml(name, content, parents).toLines()
        val createFile = CreateFileCommand(path, lines)
        return listOf(createFile)
    }

    private fun createContent(analysis: Analysis): List<HtmlElement> {
        val entryPoints = analysis.entryPoints
        val listElement = HtmlElementUtil.bigList(entryPoints, ::nameToElement)
        return listOf(listElement)
    }

    private fun nameToElement(name: String): HtmlElement =
        HtmlElementUtil.anchor(name, "local-$name.html")
}
