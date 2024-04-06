package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ListUtil.startsWith
import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit

data class Analysis(
    val global: ScopedAnalysis,
    val nameUriList: List<Pair<String, String>>,
    val lineage: Lineage,
    val groupScopedAnalysisList: List<Pair<List<String>, ScopedAnalysis>>,
    val errors: Errors,
    val summary: Summary
) {
    private val scopedAnalysisByGroup = groupScopedAnalysisList.associateBy { it.first }
    private val uriByName = nameUriList.toMap()
    fun descendantCount(group: List<String>): Int =
        groupScopedAnalysisList.filter { it.first.startsWith(group) }.sumOf { it.second.names.size }

    fun containsGroup(group: List<String>): Boolean = scopedAnalysisByGroup.containsKey(group)
    fun lookupUri(name: String): String = uriByName.getValue(name)
    fun reasonsForDependency(groupPath: List<String>, reference: Pair<String, String>): List<Pair<String, String>> {
        val (firstReference, secondReference) = reference
        val firstCodeUnit = CodeUnit(groupPath + firstReference.toCodeUnit().parts)
        val secondCodeUnit = CodeUnit(groupPath + secondReference.toCodeUnit().parts)
        val result = global.references.filter { (first, second) ->
            first.toCodeUnit().parts.startsWith(firstCodeUnit.parts) &&
                    second.toCodeUnit().parts.startsWith(secondCodeUnit.parts)
        }
        return result
    }
}
