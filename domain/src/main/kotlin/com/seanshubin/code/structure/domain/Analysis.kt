package com.seanshubin.code.structure.domain

data class Analysis(
    val global: ScopedAnalysis,
    val byGroup: List<Pair<List<String>, ScopedAnalysis>>
)
