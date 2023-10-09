package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.bytecodeformat.BinaryDetail
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import org.w3c.dom.html.HTMLLIElement
import java.nio.file.Path

class BinariesReport : HtmlReport() {
    override fun generate(reportDir: Path, analysis: Analysis): List<CreateFileCommand> {
        val name = "Binaries"
        val htmlInsideBody = generateHtml(analysis)
        val html = wrapInTopLevelHtml(name, htmlInsideBody)
        val fileName = "binaries.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    private fun generateHtml(analysis: Analysis): List<HtmlElement> {
        return summary(analysis) + table(analysis, includeDependencies = false) + table(analysis, includeDependencies = true)
    }

    private fun table(analysis: Analysis, includeDependencies:Boolean): List<HtmlElement> {
        val thead = thead(includeDependencies)
        val tbody = tbody(analysis, includeDependencies)
        val captionText = if(includeDependencies) "Dependencies" else "Binaries"
        val caption = Tag("h2", Text(captionText))
        val table = Tag("table", thead, tbody)
        return listOf(caption, table)
    }

    private fun summary(analysis: Analysis): List<HtmlElement> {
        return listOf(
            Tag("p", Text("binary count: ${analysis.observations.binaries.size}"))
        )
    }

    private fun thead(includeDependencies: Boolean): HtmlElement {
        val name = Tag("th", Text("name"))
        val elements = if(includeDependencies){
            val dependency = Tag("th", Text("dependency"))
            listOf(name, dependency)
        } else {
            val location = Tag("th", Text("location"))
            listOf(name, location)
        }
        val row = Tag("tr", elements)
        return Tag("thead", row)
    }

    private fun tbody(analysis: Analysis, includeDependencies: Boolean): HtmlElement {
        val rows = if(includeDependencies) {
            analysis.observations.binaries.flatMap { binary ->
                binaryRowsWithDependencies(binary)
            }
        } else {
            analysis.observations.binaries.map { binary ->
                binaryRowWithoutDependencies(binary)
            }
        }
        return Tag("tbody", rows)
    }

    private fun binaryRowsWithDependencies(binary: BinaryDetail): List<HtmlElement> =
        binary.dependencyNames.map{
            binaryRowWithDependencies(binary, it)
        }

    private fun binaryRowWithDependencies(binary: BinaryDetail, dependencyName:String): HtmlElement {
        val binaryCells = binaryCellsWithDependencies(binary, dependencyName)
        val binaryRow = Tag("tr", binaryCells)
        return binaryRow
    }

    private fun binaryRowWithoutDependencies(binary: BinaryDetail): HtmlElement {
        val binaryCells = binaryCellsWithoutDependencies(binary)
        val binaryRow = Tag("tr", binaryCells)
        return binaryRow
    }

    private fun binaryCellsWithoutDependencies(binary:BinaryDetail):List<HtmlElement>{
        return listOf(
            binaryCell(binary.name),
            binaryCell(binary.location),
        )
    }

    private fun binaryCellsWithDependencies(binary:BinaryDetail, dependencyName:String):List<HtmlElement>{
        return listOf(
            binaryCell(binary.name),
            binaryCell(dependencyName)
        )
    }

    private fun binaryCell(text:String):HtmlElement =
        Tag("td", Text(text))
}
