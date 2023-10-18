package com.seanshubin.code.structure.domain

data class Analysis(
    val observations: Observations,
    val cycles: List<List<String>>,
    val names: List<String>,
    val references: List<Pair<String, String>>,
    val entryPoints: List<String>,
    val cycleDetails: List<CycleDetail>,
    val detailByName: Map<String, Detail>,
    val errors: Errors?
) {
    fun referencesForScope(scope: Set<String>): Set<Pair<String, String>> {
        return scope.flatMap { referencesForScopeSingle(it, scope) }.toSet()
    }

    fun referencesForScopeSingle(name: String, scope: Set<String>): Set<Pair<String, String>> {
        val detail = detailByName.getValue(name)
        val referencesOut = detail.arrows.directionOut.all.filter { scope.contains(it) }.map {
            name to it
        }.toSet()
        val referencesIn = detail.arrows.directionIn.all.filter { scope.contains(it) }.map {
            it to name
        }
        return referencesOut + referencesIn
    }

    companion object {
        val referenceComparator = Comparator
            .comparing<Pair<String, String>, String> { it.first }
            .thenComparing(Comparator.comparing { it.second })
    }
}
