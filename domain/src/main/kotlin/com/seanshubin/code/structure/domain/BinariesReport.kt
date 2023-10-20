package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.binaryparser.BinaryDetail
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElement.Tag
import com.seanshubin.code.structure.html.HtmlElement.Text
import java.nio.file.Path

class BinariesReport : Report {
    override fun generate(reportDir: Path, validated: Validated): List<CreateFileCommand> {
        val parents = listOf(Pages.tableOfContents)
        val name = Pages.binaries.name
        val htmlInsideBody = generateHtml(validated.observations)
        val html = ReportHelper.wrapInTopLevelHtml(name, htmlInsideBody, parents)
        val path = Pages.binaries.reportFilePath(reportDir)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    private fun generateHtml(observations: Observations): List<HtmlElement> {
        return summary(observations) + table(observations, includeDependencies = false) + table(
            observations,
            includeDependencies = true
        )
    }

    private fun table(observations: Observations, includeDependencies: Boolean): List<HtmlElement> {
        val thead = thead(includeDependencies)
        val tbody = tbody(observations, includeDependencies)
        val captionText = if (includeDependencies) "Dependencies" else "Binaries"
        val caption = Tag("h2", Text(captionText))
        val table = Tag("table", thead, tbody)
        return listOf(caption, table)
    }

    private fun summary(observations: Observations): List<HtmlElement> {
        return listOf(
            Tag("p", Text("binary count: ${observations.binaries.size}"))
        )
    }

    private fun thead(includeDependencies: Boolean): HtmlElement {
        val name = Tag("th", Text("name"))
        val elements = if (includeDependencies) {
            val dependency = Tag("th", Text("dependency"))
            listOf(name, dependency)
        } else {
            val location = Tag("th", Text("location"))
            listOf(name, location)
        }
        val row = Tag("tr", elements)
        return Tag("thead", row)
    }

    private fun tbody(observations: Observations, includeDependencies: Boolean): HtmlElement {
        val rows = if (includeDependencies) {
            observations.binaries.flatMap { binary ->
                binaryRowsWithDependencies(binary)
            }
        } else {
            observations.binaries.map { binary ->
                binaryRowWithoutDependencies(binary)
            }
        }
        return Tag("tbody", rows)
    }

    private fun binaryRowsWithDependencies(binary: BinaryDetail): List<HtmlElement> =
        binary.dependencyNames.map {
            binaryRowWithDependencies(binary, it)
        }

    private fun binaryRowWithDependencies(binary: BinaryDetail, dependencyName: String): HtmlElement {
        val binaryCells = binaryCellsWithDependencies(binary, dependencyName)
        val binaryRow = Tag("tr", binaryCells)
        return binaryRow
    }

    private fun binaryRowWithoutDependencies(binary: BinaryDetail): HtmlElement {
        val binaryCells = binaryCellsWithoutDependencies(binary)
        val binaryRow = Tag("tr", binaryCells)
        return binaryRow
    }

    private fun binaryCellsWithoutDependencies(binary: BinaryDetail): List<HtmlElement> {
        return listOf(
            binaryCell(binary.name),
            binaryCell(binary.location),
        )
    }

    private fun binaryCellsWithDependencies(binary: BinaryDetail, dependencyName: String): List<HtmlElement> {
        return listOf(
            binaryCell(binary.name),
            binaryCell(dependencyName)
        )
    }

    private fun binaryCell(text: String): HtmlElement =
        Tag("td", Text(text))
}
