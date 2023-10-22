package com.seanshubin.code.structure.console

import java.nio.file.Path

data class RegressionSummary(
    val missing: List<Path>,
    val extra: List<Path>,
    val different: List<Path>
) {
    fun regressionCount(): Int = missing.size + extra.size + different.size
    fun regressionString(): String = toSummaryLines().joinToString("\n")
    fun toSummaryLines(): List<String> =
        listOf("missing (${missing.size})") +
                missing.map { "  $it " } +
                listOf("extra (${extra.size})") +
                extra.map { "  $it" } +
                listOf("different (${different.size})") +
                different.map { "  $it" }
}
