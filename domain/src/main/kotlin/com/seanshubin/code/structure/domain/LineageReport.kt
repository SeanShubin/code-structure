package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ComparatorUtil.pairComparator
import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.html.BigListClassName
import com.seanshubin.code.structure.html.HtmlElement
import com.seanshubin.code.structure.html.HtmlElementUtil.anchor
import com.seanshubin.code.structure.html.HtmlElementUtil.bigList
import java.nio.file.Path

class LineageReport(
    private val page: Page,
    private val direction: (Lineage) -> List<Pair<String, String>>
) : Report {
    override fun generate(reportDir: Path, validated: Validated): List<Command> {
        val parents = listOf(Page.tableOfContents)
        val htmlInsideBody = generateHtml(validated)
        val html = ReportHelper.wrapInTopLevelHtml(page.caption, htmlInsideBody, parents)
        val path = reportDir.resolve(page.file)
        val lines = html.toLines()
        val topCommand = CreateFileCommand(path, lines)
        return listOf(topCommand)
    }

    private fun generateHtml(validated: Validated): List<HtmlElement> {
        val codeUnitElementFunction = createCodeUnitElementFunction(validated.analysis.global.names)
        val lineageElementFunction = createLineageElementFunction(codeUnitElementFunction)
        val lineage: List<Pair<String, String>> = direction(validated.analysis.lineage)
        val configuredErrors = validated.observations.configuredErrors
        val differences = if(configuredErrors == null){
            emptyList()
        } else {
            differencesElement(lineage, direction(configuredErrors.lineage), lineageElementFunction)
        }
        return differences + lineageElement(lineage)
    }

    private fun differencesElement(
        fromAnalysis: List<Pair<String, String>>,
        fromConfiguredErrors: List<Pair<String, String>>,
        lineageElementFunction:(Pair<String,String>)->List<HtmlElement>
    ):List<HtmlElement> {
        val configured = fromConfiguredErrors.toSet()
        val existing = fromAnalysis.toSet()
        val newEntries = (existing - configured).toList().sortedWith(pairComparator)
        val fixedEntries = (configured - existing).toList().sortedWith(pairComparator)
        return differentLineageElement("New", newEntries, lineageElementFunction) + differentLineageElement("Fixed", fixedEntries, lineageElementFunction)
    }

    private fun differentLineageElement(
        caption:String,
        lineage:List<Pair<String, String>>,
        elementFunction:(Pair<String, String>)->List<HtmlElement>
    ):List<HtmlElement>{
        if(lineage.isEmpty()) return emptyList()
        val header = HtmlElement.tagText("h2", caption)
        val list = bigList(
            lineage,
            elementFunction,
            BigListClassName.COLUMN_2,
            "lineage"
        )
        return listOf(header) + list
    }

    private fun lineageElement(references: List<Pair<String, String>>): List<HtmlElement> {
        val captionElement = HtmlElement.Tag("h2", listOf(HtmlElement.Text(page.caption)))
        val referencesElement = bigList(references, ::referenceToElements, BigListClassName.COLUMN_2, page.caption)
        return listOf(captionElement) + referencesElement
    }

    private fun referenceToElements(reference: Pair<String, String>): List<HtmlElement> =
        listOf(nameToElement(reference.first), nameToElement(reference.second))

    private fun nameToElement(name: String): HtmlElement {
        val link = name.toCodeUnit().toUriName("local", ".html")
        val anchor = anchor(name, link)
        val inSpan = listOf(anchor)
        val span = HtmlElement.Tag("span", inSpan)
        return span
    }

    private fun codeUnitElement(name: String): List<HtmlElement> {
        val link = name.toCodeUnit().toUriName("local", ".html")
        return listOf(anchor(name, link))
    }

    private fun codeUnitElementThatDoesNotExist(name: String): List<HtmlElement> {
        return listOf(HtmlElement.tagText("span", "$name (no longer exists)"))
    }

    private fun createCodeUnitElementFunction(existingNames:List<String>):(name:String) -> List<HtmlElement> = {name:String ->
        if(existingNames.contains(name)) codeUnitElement(name)
        else codeUnitElementThatDoesNotExist(name)
    }

    private fun createLineageElementFunction(codeUnitElementFunction:(String)->List<HtmlElement>):(lineage:Pair<String, String>) -> List<HtmlElement> = {lineage ->
        lineage.toList().flatMap(codeUnitElementFunction)
    }
}
