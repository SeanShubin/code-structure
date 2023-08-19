package com.seanshubin.code.structure.domain

interface Analyzer {
    fun analyze(observations: Observations): Analysis
}
