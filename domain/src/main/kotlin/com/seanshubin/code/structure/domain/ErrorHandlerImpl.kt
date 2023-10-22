package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ComparatorUtil.pairComparator
import com.seanshubin.code.structure.collection.ComparatorUtil.stringComparator
import com.seanshubin.code.structure.collection.SetUtil
import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.domain.ErrorsDto.Companion.toDto
import com.seanshubin.code.structure.json.JsonMappers
import java.nio.file.Path

class ErrorHandlerImpl(
    private val files: FilesContract,
    private val errorFilePath: Path,
    private val errorReportEvent: (List<String>) -> Unit
) : ErrorHandler {
    override fun handleErrors(old: Errors?, current: Errors, failConditions: FailConditions): Int {
        return if (old == null) {
            if (current.hasErrors()) {
                defineCurrentStateAsAcceptable(current)
            }
            0
        } else {
            compareErrors(old, current, failConditions)
        }
    }

    private fun defineCurrentStateAsAcceptable(current: Errors) {
        val text = JsonMappers.pretty.writeValueAsString(current.toDto())
        files.writeString(errorFilePath, text)
    }

    private fun compareErrors(old: Errors, current: Errors, failConditions: FailConditions): Int {
        val directCycles = compareReport(
            "Direct Cycles",
            failConditions.directCycle,
            old.inDirectCycle,
            current.inDirectCycle,
            stringComparator,
            stringFormat
        )
        val groupCycles = compareReport(
            "Group Cycles",
            failConditions.groupCycle,
            old.inGroupCycle,
            current.inGroupCycle,
            stringComparator,
            stringFormat
        )
        val ancestorDependsOnDescendant = compareReport(
            "Ancestor depends on Descendant",
            failConditions.ancestorDependsOnDescendant,
            old.ancestorDependsOnDescendant,
            current.ancestorDependsOnDescendant,
            pairComparator,
            referenceFormat
        )
        val descendantDependsOnAncestor = compareReport(
            "Descendant depends on Ancestor",
            failConditions.descendantDependsOnAncestor,
            old.descendantDependsOnAncestor,
            current.descendantDependsOnAncestor,
            pairComparator,
            referenceFormat
        )
        val lines = directCycles.lines+
                groupCycles.lines +
                ancestorDependsOnDescendant.lines +
                descendantDependsOnAncestor.lines
        errorReportEvent(lines)
        val exitCode = if(directCycles.fail || groupCycles.fail || ancestorDependsOnDescendant.fail || descendantDependsOnAncestor.fail){
            1
        } else {
            0
        }
        return exitCode
    }

    private fun <T> compareReport(
        caption: String,
        failPossible: Boolean,
        old: List<T>,
        current: List<T>,
        comparator: Comparator<T>,
        format: (T) -> String
    ): CompareReport {
        val compare = SetUtil.compare(old.toSet(), current.toSet())
        if (compare.isSame()) return CompareReport(fail = false, emptyList())
        val addedList = compare.extra.toList().sortedWith(comparator).map(format)
        val removedList = compare.missing.toList().sortedWith(comparator).map(format)
        val lines = listOf(caption) + captionList("added", addedList) + captionList("removed", removedList)
        val fail = failPossible && addedList.isNotEmpty()
        return CompareReport(fail, lines)
    }

    private fun captionList(caption: String, list: List<String>): List<String> =
        if (list.isEmpty()) emptyList()
        else listOf("  $caption") + list.map { "    $it" }

    private val stringFormat: (String) -> String = { it }
    private val referenceFormat: (Pair<String, String>) -> String = { (first, second) -> "$first -> $second" }

    private data class CompareReport(val fail: Boolean, val lines: List<String>)
}
