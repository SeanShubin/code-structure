package com.seanshubin.code.structure.domain

data class ScopedAnalysis(
    val cycles: List<List<String>>,
    val names: List<String>,
    val references: List<Pair<String, String>>,
    val entryPoints: List<String>,
    val cycleDetails: List<CycleDetail>,
    val details: List<Detail>
) {
    private val detailByName = details.associateBy { it.name }
    fun referencesForScope(scope: Set<String>): Set<Pair<String, String>> {
        return scope.flatMap { referencesForScopeSingle(it, scope) }.toSet()
    }

    fun referencesForScopeSingle(name: String, scope: Set<String>): Set<Pair<String, String>> {
        val detail = detailByName.getValue(name)
        val referencesOut = detail.arrows.directionOut.filter { scope.contains(it) }.map {
            name to it
        }.toSet()
        val referencesIn = detail.arrows.directionIn.filter { scope.contains(it) }.map {
            it to name
        }
        return referencesOut + referencesIn
    }

    fun lookupDetail(name: String): Detail = detailByName.getValue(name)
}
