package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit

data class ScopedObservations(
    val groupPath: List<String>,
    val referenceReasons: Map<Pair<String, String>, List<Pair<String, String>>>
) {
    companion object {
        fun create(references: List<Pair<String, String>>): List<ScopedObservations> {
            return create(emptyList(), references)
        }

        private fun create(
            currentGroup: List<String>,
            references: List<Pair<String, String>>
        ): List<ScopedObservations> {
            val (subGroupReferences, groupReferences) = references.groupBy { (first, second) ->
                oneLevelDownFromGroup(currentGroup, first) to oneLevelDownFromGroup(currentGroup, second)
            }.toList().partition { it.first.first == it.first.second }
            val currentReferenceReasons = groupReferences.toMap()
            val currentScopedObservations = ScopedObservations(currentGroup, currentReferenceReasons)
            val subGroupObservationsList = subGroupReferences.flatMap { (identicalPair, allReasons) ->
                val subGroupPath = identicalPair.first.toCodeUnit().parts
                val target = CodeUnit(subGroupPath).toName()
                val reasons = allReasons.filterNot {
                    val firstName = it.first
                    val secondName = it.second
                    val booleanResult = firstName == target || secondName == target
                    booleanResult
                }
                create(subGroupPath, reasons)
            }
            return listOf(currentScopedObservations) + subGroupObservationsList
        }

        private fun oneLevelDownFromGroup(groupPath: List<String>, name: String): String {
            val result = CodeUnit(name.toCodeUnit().parts.subList(0, groupPath.size + 1)).toName()
            return result
        }
    }
}
