package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import java.nio.file.Path

class CycleReport : HtmlReport() {
    override fun generate(reportDir: Path, analysis: Analysis): List<CreateFileCommand> {
        val name = "Cycles"
        val htmlInsideBody = generateHtml(analysis)
        val html = wrapInTopLevelHtml(name, htmlInsideBody)
        val fileName = "cycles.html"
        val path = reportDir.resolve(fileName)
        val lines = html.toLines()
        return listOf(CreateFileCommand(path, lines))
    }

    private fun generateHtml(analysis: Analysis): List<HtmlElement> {
        return summaryElement(analysis) + cyclesElement(analysis)
    }

    private fun summaryElement(analysis: Analysis): List<HtmlElement> {
        return listOf(
            HtmlElement.Tag("p", HtmlElement.Text("cycle count: ${analysis.cycles.size}"))
        )
    }

    private fun cyclesElement(analysis: Analysis): List<HtmlElement> {
        return analysis.cycles.flatMapIndexed(::cycleListElement)
    }

    private fun cycleListElement(listIndex: Int, cycleList: List<String>): List<HtmlElement> {
        val summaryText = HtmlElement.Text("cycle-$listIndex")
        val summary = HtmlElement.Tag("h2", listOf(summaryText))
        val partCountText = HtmlElement.Text("part count: ${cycleList.size}")
        val partCountParagraph = HtmlElement.Tag("p", listOf(partCountText))
        val cycleElements = cycleList.mapIndexed { nameIndex, name -> cycleElement(listIndex, nameIndex, name) }
        val inlineFlexCycleElements = inlineFlexDiv(cycleElements)
        return listOf(summary, partCountParagraph) + inlineFlexCycleElements
    }

    private fun cycleElement(listIndex: Int, nameIndex: Int, name: String): HtmlElement {
        val text = HtmlElement.Text(name)
        val link = "cycle-$listIndex-$nameIndex-$name"
        val anchor = HtmlElement.Tag(
            "a", listOf(text), listOf(
                "href" to link
            )
        )
        return anchor
    }

    private fun inlineFlexDiv(children: List<HtmlElement>): HtmlElement {
        return HtmlElement.Tag(
            "div", children, listOf(
                "class" to "big-list"
            )
        )
    }
}
