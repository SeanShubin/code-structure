package com.seanshubin.code.structure.domain

class ValidatorImpl:Validator {
    override fun validate(observations: Observations, analysis: Analysis): Validated {
        val errors = composeErrors(analysis.global.cycles, observations.oldInCycle)
        return Validated(observations, analysis, errors)
    }
    private fun composeErrors(cycles: List<List<String>>, oldInCycle: List<String>): Errors? {
        val currentInCycle = cycles.flatten().distinct().toSet()
        val newInCycle = currentInCycle - oldInCycle.distinct().toSet()
        val errors = if (newInCycle.isEmpty()) null else Errors(newInCycle.toList().sorted())
        return errors
    }
}
