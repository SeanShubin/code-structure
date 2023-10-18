package com.seanshubin.code.structure.domain

data class Analysis(
    val global: ScopedAnalysis,
    val byGroup: Map<List<String>, ScopedAnalysis>
){
    fun descendantCount(group:List<String>):Int {
        val keys = byGroup.keys.filter { it.startsWith(group) }
        return keys.sumOf { byGroup.getValue(it).names.size }
    }

    private fun <T> List<T>.startsWith(other:List<T>):Boolean {
        if(other.size > size) return false
        return this.take(other.size) == other
    }
}
