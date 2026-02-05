package com.seanshubin.code.structure.composition

import com.seanshubin.code.structure.config.JsonFileKeyValueStore
import com.seanshubin.code.structure.config.KeyValueStore
import com.seanshubin.code.structure.config.KeyValueStoreWithDocumentation
import com.seanshubin.code.structure.config.KeyValueStoreWithDocumentationDelegate
import com.seanshubin.code.structure.config.TypeUtil.coerceToBoolean
import com.seanshubin.code.structure.config.TypeUtil.coerceToInt
import com.seanshubin.code.structure.config.TypeUtil.coerceToListOfString
import com.seanshubin.code.structure.config.TypeUtil.coerceToPath
import com.seanshubin.code.structure.config.TypeUtil.coerceToString
import com.seanshubin.code.structure.runtime.CountAsErrors
import java.nio.file.Paths

class ConfigurationLoader(
    private val integrations: Integrations,
    private val configBaseName: String
) {
    fun load(): Configuration {
        val configFile = Paths.get("$configBaseName-config.json")
        val configDocumentationFile = Paths.get("$configBaseName-documentation.json")
        val keyValueStore: KeyValueStore = JsonFileKeyValueStore(configFile, integrations.files)
        val documentationKeyValueStore: KeyValueStore =
            JsonFileKeyValueStore(configDocumentationFile, integrations.files)
        val config: KeyValueStoreWithDocumentation =
            KeyValueStoreWithDocumentationDelegate(keyValueStore, documentationKeyValueStore)

        // Initialize _documentation metadata
        config.load(
            listOf("_documentation", "description"),
            "Analyzes dependency structure of source code",
            ConfigDocumentation.documentationDescription
        )
        config.load(
            listOf("_documentation", "readme"),
            "https://github.com/SeanShubin/code-structure/blob/master/README.md",
            ConfigDocumentation.documentationReadme
        )
        config.load(
            listOf("_documentation", "configHelp"),
            "$configBaseName-documentation.json",
            ConfigDocumentation.documentationConfigHelp
        )

        val countAsErrors = CountAsErrors(
            inDirectCycle =
                config.load(listOf("countAsErrors", "inDirectCycle"), true, ConfigDocumentation.inDirectCycle)
                    .coerceToBoolean(),
            inGroupCycle =
                config.load(listOf("countAsErrors", "inGroupCycle"), true, ConfigDocumentation.inGroupCycle)
                    .coerceToBoolean(),
            ancestorDependsOnDescendant =
                config.load(
                    listOf("countAsErrors", "ancestorDependsOnDescendant"),
                    true,
                    ConfigDocumentation.ancestorDependsOnDescendant
                ).coerceToBoolean(),
            descendantDependsOnAncestor =
                config.load(
                    listOf("countAsErrors", "descendantDependsOnAncestor"),
                    true,
                    ConfigDocumentation.descendantDependsOnAncestor
                ).coerceToBoolean(),
        )
        val maximumAllowedErrorCount: Int =
            config.load(listOf("maximumAllowedErrorCount"), 0, ConfigDocumentation.maximumAllowedErrorCount)
                .coerceToInt()
        val inputDir = config.load(listOf("inputDir"), ".", ConfigDocumentation.inputDir).coerceToPath()
        val outputDir =
            config.load(listOf("outputDir"), "generated/code-structure", ConfigDocumentation.outputDir).coerceToPath()
        val useObservationsCache =
            config.load(listOf("useObservationsCache"), false, ConfigDocumentation.useObservationsCache)
                .coerceToBoolean()
        val includeJvmDynamicInvocations =
            config.load(listOf("includeJvmDynamicInvocations"), false, ConfigDocumentation.includeJvmDynamicInvocations)
                .coerceToBoolean()
        val sourcePrefix =
            config.load(listOf("sourcePrefix"), "prefix for link to source code", ConfigDocumentation.sourcePrefix)
                .coerceToString()
        val sourceFileIncludeRegexPatterns: List<String> =
            config.load(
                listOf("sourceFileRegexPatterns", "include"),
                emptyList<String>(),
                ConfigDocumentation.sourceFileRegexPatternsInclude
            ).coerceToListOfString()
        val sourceFileExcludeRegexPatterns: List<String> =
            config.load(
                listOf("sourceFileRegexPatterns", "exclude"),
                emptyList<String>(),
                ConfigDocumentation.sourceFileRegexPatternsExclude
            ).coerceToListOfString()
        val nodeLimitForGraph: Int =
            config.load(listOf("nodeLimitForGraph"), 100, ConfigDocumentation.nodeLimitForGraph).coerceToInt()
        val binaryFileIncludeRegexPatterns: List<String> =
            config.load(
                listOf("binaryFileRegexPatterns", "include"),
                emptyList<String>(),
                ConfigDocumentation.binaryFileRegexPatternsInclude
            ).coerceToListOfString()
        val binaryFileExcludeRegexPatterns: List<String> =
            config.load(
                listOf("binaryFileRegexPatterns", "exclude"),
                emptyList<String>(),
                ConfigDocumentation.binaryFileRegexPatternsExclude
            ).coerceToListOfString()

        return Configuration(
            countAsErrors = countAsErrors,
            maximumAllowedErrorCount = maximumAllowedErrorCount,
            inputDir = inputDir,
            outputDir = outputDir,
            useObservationsCache = useObservationsCache,
            includeJvmDynamicInvocations = includeJvmDynamicInvocations,
            sourcePrefix = sourcePrefix,
            sourceFileIncludeRegexPatterns = sourceFileIncludeRegexPatterns,
            sourceFileExcludeRegexPatterns = sourceFileExcludeRegexPatterns,
            nodeLimitForGraph = nodeLimitForGraph,
            binaryFileIncludeRegexPatterns = binaryFileIncludeRegexPatterns,
            binaryFileExcludeRegexPatterns = binaryFileExcludeRegexPatterns
        )
    }
}
