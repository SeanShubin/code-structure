package com.seanshubin.code.structure.injection

import com.seanshubin.code.structure.appconfig.CountAsErrors
import java.nio.file.Path

data class Configuration(
    val countAsErrors: CountAsErrors,
    val maximumAllowedErrorCount: Int,
    val inputDir: Path,
    val outputDir: Path,
    val useObservationsCache: Boolean,
    val includeJvmDynamicInvocations: Boolean,
    val sourcePrefix: String,
    val sourceFileIncludeRegexPatterns: List<String>,
    val sourceFileExcludeRegexPatterns: List<String>,
    val nodeLimitForGraph: Int,
    val binaryFileIncludeRegexPatterns: List<String>,
    val binaryFileExcludeRegexPatterns: List<String>
)
