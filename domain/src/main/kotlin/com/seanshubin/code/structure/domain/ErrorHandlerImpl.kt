package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ComparatorUtil
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
    override fun handleErrors(old: Errors?, current: Errors, failConditions: FailConditions): String? {
        return if (old == null) {
            if (current.hasErrors()) {
                defineCurrentStateAsAcceptable(current)
            }
            null
        } else {
            compareErrors(old, current, failConditions)
        }
    }

    private fun defineCurrentStateAsAcceptable(current: Errors) {
        val text = JsonMappers.pretty.writeValueAsString(current.toDto())
        files.writeString(errorFilePath, text)
    }

    private fun compareErrors(old: Errors, current: Errors, failConditions: FailConditions): String? {
        val errorInfo = ErrorInfo(old, current, failConditions)
        val errorReports = ErrorDimension.values().map { it.analyze(errorInfo) }
        val anyFailed = errorReports.any { it.isFail }
        val errorMessage = if (anyFailed) "There were failures" else null
        val lines = errorReports.flatMap { it.lines }
        errorReportEvent(lines)
        return errorMessage
    }

    data class ErrorReport(val isFail: Boolean, val lines: List<String>)

    class ErrorInfo(val old: Errors, val current: Errors, val failConditions: FailConditions)

    interface ErrorElement : Comparable<ErrorElement> {
        fun formatted(): String
    }

    data class StringErrorElement(val value: String) : ErrorElement {
        override fun compareTo(other: ErrorElement): Int {
            other as StringErrorElement
            return ComparatorUtil.stringComparator.compare(this.value, other.value)
        }

        override fun formatted(): String = value
    }

    data class PairErrorElement(val value: Pair<String, String>) : ErrorElement {
        override fun compareTo(other: ErrorElement): Int {
            other as PairErrorElement
            return ComparatorUtil.pairComparator.compare(this.value, other.value)
        }

        override fun formatted(): String {
            val (first, second) = value
            return "$first -> $second"

        }
    }


    enum class ErrorDimension(val caption: String) {
        DIRECT_CYCLE("Direct Cycle") {
            override fun fetchElements(errors: Errors): Set<ErrorElement> {
                return errors.inDirectCycle.map { StringErrorElement(it) }.toSet()
            }

            override fun fetchCanFail(failConditions: FailConditions): Boolean {
                return failConditions.directCycle
            }
        },
        GROUP_CYCLE("Group Cycle") {
            override fun fetchElements(errors: Errors): Set<ErrorElement> {
                return errors.inGroupCycle.map { StringErrorElement(it) }.toSet()
            }

            override fun fetchCanFail(failConditions: FailConditions): Boolean {
                return failConditions.groupCycle
            }
        },
        ANCESTOR_DEPENDS_ON_DESCENDANT("Ancestor Depends On Descendant") {
            override fun fetchElements(errors: Errors): Set<ErrorElement> {
                return errors.ancestorDependsOnDescendant.map { PairErrorElement(it) }.toSet()
            }

            override fun fetchCanFail(failConditions: FailConditions): Boolean {
                return failConditions.ancestorDependsOnDescendant
            }
        },
        DESCENDANT_DEPENDS_ON_ANCESTOR("Descendant Depends On Ancestor") {
            override fun fetchElements(errors: Errors): Set<ErrorElement> {
                return errors.descendantDependsOnAncestor.map { PairErrorElement(it) }.toSet()
            }

            override fun fetchCanFail(failConditions: FailConditions): Boolean {
                return failConditions.descendantDependsOnAncestor
            }
        };

        fun analyze(errorInfo: ErrorInfo): ErrorReport {
            val old = fetchElements(errorInfo.old)
            val current = fetchElements(errorInfo.current)
            val compareResult = SetUtil.compare(old, current)
            val canFail = fetchCanFail(errorInfo.failConditions)
            val newErrors = compareResult.extra.toList().sorted().distinct()
            val hasNewErrors = newErrors.isNotEmpty()
            val isFail = canFail && hasNewErrors
            val lines: List<String> = if (hasNewErrors) {
                val newFailuresHeader = "ERROR: New failures in $caption: ${newErrors.size}"
                val newFailuresLines = newErrors.map { "  ${it.formatted()}" }
                listOf(newFailuresHeader) + newFailuresLines
            } else {
                emptyList()
            }
            return ErrorReport(isFail, lines)
        }

        abstract fun fetchElements(errors: Errors): Set<ErrorElement>
        abstract fun fetchCanFail(failConditions: FailConditions): Boolean
    }
}
