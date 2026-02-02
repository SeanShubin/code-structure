package com.seanshubin.code.structure.pipeline

import com.seanshubin.code.structure.model.Analysis
import com.seanshubin.code.structure.model.Observations

interface Analyzer {
    fun analyze(observations: Observations): Analysis
}
