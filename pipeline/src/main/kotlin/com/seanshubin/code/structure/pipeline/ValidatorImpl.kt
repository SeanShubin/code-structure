package com.seanshubin.code.structure.pipeline

import com.seanshubin.code.structure.model.Analysis
import com.seanshubin.code.structure.model.Observations
import com.seanshubin.code.structure.model.Validated

class ValidatorImpl : Validator {
    override fun validate(observations: Observations, analysis: Analysis): Validated {
        return Validated(observations, analysis)
    }
}
