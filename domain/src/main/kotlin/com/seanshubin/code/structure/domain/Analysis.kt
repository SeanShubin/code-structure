package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ListUtil.startsWith

data class Analysis(
    val global: ScopedAnalysis,
    val lineage: Lineage,
    val byGroup: Map<List<String>, ScopedAnalysis>
){
    fun descendantCount(group:List<String>):Int {
        val keys = byGroup.keys.filter { it.startsWith(group) }
        return keys.sumOf { byGroup.getValue(it).names.size }
    }
}
