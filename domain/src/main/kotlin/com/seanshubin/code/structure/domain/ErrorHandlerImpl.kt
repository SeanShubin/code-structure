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
    private val maximumAllowedErrorCount: Int,
    private val errorReportEvent: (List<String>) -> Unit
) : ErrorHandler {
    override fun handleErrors(old: Errors?, current: Errors, countAsErrors: CountAsErrors): String? {
        return if (old == null) {
            if (current.hasErrors()) {
                defineCurrentStateAsAcceptable(current)
            }
            null
        } else {
            compareErrors(old, current, countAsErrors)
        }
    }

    private fun defineCurrentStateAsAcceptable(current: Errors) {
        val text = JsonMappers.pretty.writeValueAsString(current.toDto())
        files.writeString(errorFilePath, text)
    }

    private fun compareErrors(old: Errors, current: Errors, countAsErrors: CountAsErrors): String? {
        val errorInfo = ErrorInfo(old, current, countAsErrors)
        val errorReports = ErrorDimension.entries.map { it.analyze(errorInfo) }
        val totalCanFailErrors = errorReports.sumOf { it.canFailErrorCount }
        val anyFailed = errorReports.any { it.isFail }
        val errorReportLines = errorReports.flatMap { it.lines }
        val errorMessage = if (anyFailed) {
            "There are new errors"
        } else if (totalCanFailErrors > maximumAllowedErrorCount) {
            "Exceeded maximum allowable errors.  Got $totalCanFailErrors.  Limit is $maximumAllowedErrorCount"
        } else {
            null
        }
        val totalFailureLines = if (errorMessage == null) {
            emptyList()
        } else {
            listOf(errorMessage)
        }
        val lines = errorReportLines + totalFailureLines
        errorReportEvent(lines)
        return errorMessage
    }

    data class ErrorReport(val isFail: Boolean, val lines: List<String>, val canFailErrorCount: Int)

    class ErrorInfo(val old: Errors, val current: Errors, val countAsErrors: CountAsErrors)

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

            override fun fetchCountAsError(countAsErrors: CountAsErrors): Boolean {
                return countAsErrors.directCycle
            }
        },
        GROUP_CYCLE("Group Cycle") {
            override fun fetchElements(errors: Errors): Set<ErrorElement> {
                return errors.inGroupCycle.map { StringErrorElement(it) }.toSet()
            }

            override fun fetchCountAsError(countAsErrors: CountAsErrors): Boolean {
                return countAsErrors.groupCycle
            }
        },
        ANCESTOR_DEPENDS_ON_DESCENDANT("Ancestor Depends On Descendant") {
            override fun fetchElements(errors: Errors): Set<ErrorElement> {
                return errors.lineage.ancestorDependsOnDescendant.map { PairErrorElement(it) }.toSet()
            }

            override fun fetchCountAsError(countAsErrors: CountAsErrors): Boolean {
                return countAsErrors.ancestorDependsOnDescendant
            }
        },
        DESCENDANT_DEPENDS_ON_ANCESTOR("Descendant Depends On Ancestor") {
            override fun fetchElements(errors: Errors): Set<ErrorElement> {
                return errors.lineage.descendantDependsOnAncestor.map { PairErrorElement(it) }.toSet()
            }

            override fun fetchCountAsError(countAsErrors: CountAsErrors): Boolean {
                return countAsErrors.descendantDependsOnAncestor
            }
        };

        fun analyze(errorInfo: ErrorInfo): ErrorReport {
            val old = fetchElements(errorInfo.old)
            val current = fetchElements(errorInfo.current)
            val compareResult = SetUtil.compare(old, current)
            val canFail = fetchCountAsError(errorInfo.countAsErrors)
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
            val canFailErrorCount = if (canFail) current.size else 0
            return ErrorReport(isFail, lines, canFailErrorCount)
        }

        abstract fun fetchElements(errors: Errors): Set<ErrorElement>
        abstract fun fetchCountAsError(countAsErrors: CountAsErrors): Boolean
    }
}
