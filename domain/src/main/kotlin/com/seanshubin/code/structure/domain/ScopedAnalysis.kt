package com.seanshubin.code.structure.domain

data class ScopedAnalysis(
    val cycles: List<List<String>>,
    val names: List<String>,
    val referenceReasons: Map<Pair<String, String>, List<Pair<String, String>>>,
    val entryPoints: List<String>,
    val cycleDetails: List<CycleDetail>,
    val details: List<Detail>,
    val isLeafGroup: Boolean
)
