package com.seanshubin.code.structure.domain

class ValidatorImpl : Validator {
    override fun validate(observations: Observations, analysis: Analysis): Validated {
        return Validated(observations, analysis)
    }
}
