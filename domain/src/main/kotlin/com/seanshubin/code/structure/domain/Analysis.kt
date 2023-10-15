package com.seanshubin.code.structure.domain

data class Analysis(
    val observations: Observations,
    val cycles: List<List<String>>,
    val names: List<String>,
    val references: List<Pair<String, String>>,
    val cycleDetails: List<CycleDetail>,
    val localDetail: Map<String, LocalDetail>,
    val errorDetail: ErrorDetail?
)
