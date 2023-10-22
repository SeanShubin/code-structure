package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.SetUtil
import com.seanshubin.code.structure.contract.FilesContract
import com.seanshubin.code.structure.domain.ErrorsDto.Companion.toDto
import com.seanshubin.code.structure.json.JsonMappers
import java.nio.file.Path

class ErrorHandlerImpl(
    private val files: FilesContract,
    private val errorFilePath: Path,
    private val errorReportEvent: (List<String>) -> Unit
) : ErrorHandler {
    override fun handleErrors(old: Errors?, current: Errors): Int {
        return if (old == null) {
            if (current.hasErrors()) {
                defineCurrentStateAsAcceptable(current)
            }
            0
        } else {
            compareErrors(old, current)
        }
    }

    private fun defineCurrentStateAsAcceptable(current: Errors) {
        val text = JsonMappers.pretty.writeValueAsString(current.toDto())
        files.writeString(errorFilePath, text)
    }

    private fun compareErrors(old: Errors, current: Errors): Int {
        val directCycles = compareReport(
            "Direct Cycles",
            old.inDirectCycle,
            current.inDirectCycle,
            stringComparator,
            stringFormat
        )
        val groupCycles = compareReport(
            "Group Cycles",
            old.inGroupCycle,
            current.inGroupCycle,
            stringComparator,
            stringFormat
        )
        val ancestorDependsOnDescendant = compareReport(
            "Ancestor depends on Descendant",
            old.ancestorDependsOnDescendant,
            current.ancestorDependsOnDescendant,
            referenceComparator,
            referenceFormat
        )
        val descendantDependsOnAncestor = compareReport(
            "Descendant depends on Ancestor",
            old.descendantDependsOnAncestor,
            current.descendantDependsOnAncestor,
            referenceComparator,
            referenceFormat
        )
        val lines = directCycles + groupCycles + ancestorDependsOnDescendant + descendantDependsOnAncestor
        errorReportEvent(lines)
        return if (lines.isEmpty()) 0 else 1
    }

    private fun <T> compareReport(
        caption: String,
        old: List<T>,
        current: List<T>,
        comparator: Comparator<T>,
        format: (T) -> String
    ): List<String> {
        val compare = SetUtil.compare(old.toSet(), current.toSet())
        if (compare.isSame()) return emptyList()
        val addedList = compare.extra.toList().sortedWith(comparator).map(format)
        val removedList = compare.missing.toList().sortedWith(comparator).map(format)
        return listOf(caption) + captionList("added", addedList) + captionList("removed", removedList)
    }

    private fun captionList(caption: String, list: List<String>): List<String> =
        if (list.isEmpty()) emptyList()
        else listOf("  $caption") + list.map { "    $it" }

    private val stringComparator: Comparator<String> = Comparator { o1, o2 -> o1.compareTo(o2) }
    private val firstComparator: Comparator<Pair<String, String>> =
        Comparator { o1, o2 -> o1.first.compareTo(o2.first) }
    private val secondComparator: Comparator<Pair<String, String>> =
        Comparator { o1, o2 -> o1.second.compareTo(o2.second) }
    private val referenceComparator = firstComparator.thenComparing(secondComparator)
    private val stringFormat: (String) -> String = { it }
    private val referenceFormat: (Pair<String, String>) -> String = { (first, second) -> "$first -> $second" }
}
