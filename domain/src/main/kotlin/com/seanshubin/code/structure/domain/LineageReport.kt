package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class LineageReport : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Page.tableOfContents)
        val htmlInsideBody = generateHtml(validated.analysis.lineage)
        val html = ReportHelper.wrapInTopLevelHtml(Page.lineage.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(Page.lineage.file)
        val lines = html.toLines()
        val topCommand = CreateFileCommand(path, lines)
        return listOf(topCommand)
    }

    private fun generateHtml(lineage: Lineage): List<HtmlElement> {
        return lineageElement("Ancestor depends on Descendant", lineage.ancestorToDescendant) +
                lineageElement("Descendant depends on Ancestor", lineage.descendantToAncestor)
    }

    private fun lineageElement(caption:String, references:List<Pair<String, String>>):List<HtmlElement>{
        val captionElement = HtmlElement.Tag("h2", listOf(HtmlElement.Text(caption)))
        val referencesElement = bigList(references, ::referenceToElements, "two-column", caption)
        return listOf(captionElement) + referencesElement
    }

    private fun referenceToElements(reference:Pair<String, String>):List<HtmlElement> =
        listOf(nameToElement(reference.first), nameToElement(reference.second))

    private fun nameToElement(name:String):HtmlElement {
        val link = "local-$name.html"
        val anchor = anchor(name, link)
        val inSpan = listOf(anchor)
        val span = HtmlElement.Tag("span", inSpan)
        return span
    }
}
