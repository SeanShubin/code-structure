package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.cycle.CycleUtil

class AnalyzerImpl : Analyzer {
    override fun analyze(observations: Observations): Analysis {
        val cycles = findCycles(observations)
        return Analysis(observations, cycles)
    }

    private fun findCycles(observations: Observations): List<List<String>> {
        val edges = observations.binaries.flatMap { binary ->
            binary.dependencyNames.map { dependency ->
                binary.name to dependency
            }
        }.toSet()
        return CycleUtil.findCycles(edges)
    }
}
