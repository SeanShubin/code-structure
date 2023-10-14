package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.dot.DotReport
import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class GraphReport:HtmlReport() {
    override fun generate(reportDir: Path, analysis: Analysis): List<Command> {
        val baseName = "graph"
        val dotSourcePath = reportDir.resolve("$baseName.txt")
        val svgPath = reportDir.resolve("$baseName.svg")
        val htmlTemplatePath = reportDir.resolve("template-$baseName.html")
        val htmlPath = reportDir.resolve("$baseName.html")
        val lines = DotReport(analysis.names, analysis.references).toLines()
        val createDotSource = CreateFileCommand(dotSourcePath, lines)
        val generateSvg = GenerateSvgCommand(dotSourcePath, svgPath)
        val substitutionTag = "---replace--with--$baseName.svg---"
        val htmlContent = htmlContent(substitutionTag)
        val htmlLines = wrapInTopLevelHtml(baseName, htmlContent).toLines()
        val createHtml = CreateFileCommand(htmlTemplatePath, htmlLines)
        val replaceCommand = SubstituteFromFileCommand(htmlTemplatePath, substitutionTag, svgPath, htmlPath)
        return listOf(createDotSource, generateSvg, createHtml, replaceCommand)
    }

    private fun htmlContent(substitutionTag:String):List<HtmlElement>{
        val divContents = HtmlElement.Text(substitutionTag)
        val div = HtmlElement.Tag("div", divContents)
        return listOf(div)
    }
}
