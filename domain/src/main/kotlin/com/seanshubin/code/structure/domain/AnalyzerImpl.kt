package com.seanshubin.code.structure.domain

class AnalyzerImpl : Analyzer {
    override fun analyze(observations: Observations): Analysis {
        return Analysis(observations)
    }
}
