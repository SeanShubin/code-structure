package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ListUtil.startsWith

data class Analysis(
    val global: ScopedAnalysis,
    val nameUriList: List<Pair<String, String>>,
    val lineage: Lineage,
    val groupScopedAnalysisList: List<Pair<List<String>, ScopedAnalysis>>,
    val summary: Summary
) {
    private val scopedAnalysisByGroup = groupScopedAnalysisList.associateBy { it.first }
    fun descendantCount(group: List<String>): Int =
        groupScopedAnalysisList.filter { it.first.startsWith(group) }.sumOf { it.second.names.size }

    fun containsGroup(group: List<String>): Boolean = scopedAnalysisByGroup.containsKey(group)
}
