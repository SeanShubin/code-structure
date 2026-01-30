package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ListUtil.startsWith
import java.nio.file.Path

data class Analysis(
    val global: ScopedAnalysis,
    val sourceByName: Map<String, List<Path>>,
    val nameUriList: List<Pair<String, String>>,
    val lineage: Lineage,
    val groupScopedAnalysisList: List<Pair<List<String>, ScopedAnalysis>>,
    val groupCycles: List<Pair<List<String>, CycleDetail>>,
    val summary: Summary
) {
    private val scopedAnalysisByGroup = groupScopedAnalysisList.associateBy { it.first }
    fun descendantCount(group: List<String>): Int =
        groupScopedAnalysisList.filter { it.first.startsWith(group) }.sumOf { it.second.names.size }

    fun containsGroup(group: List<String>): Boolean = scopedAnalysisByGroup.containsKey(group)
}
