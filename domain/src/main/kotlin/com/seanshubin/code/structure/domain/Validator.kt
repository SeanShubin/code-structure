package com.seanshubin.code.structure.domain

interface Validator {
    fun validate(observations: Observations, analysis: Analysis): Validated
}
