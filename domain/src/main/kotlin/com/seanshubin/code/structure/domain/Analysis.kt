package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ListUtil.startsWith

data class Analysis(
    val global: ScopedAnalysis,
    val ancestorToDescendant: List<Pair<String, String>>,
    val descendantToAncestor: List<Pair<String, String>>,
    val byGroup: Map<List<String>, ScopedAnalysis>
){
    fun descendantCount(group:List<String>):Int {
        val keys = byGroup.keys.filter { it.startsWith(group) }
        return keys.sumOf { byGroup.getValue(it).names.size }
    }
}
