package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit

data class ScopedObservations(
    val groupPath: List<String>,
    val names: List<String>,
    val referenceReasons: Map<Pair<String, String>, List<Pair<String, String>>>
) {
    companion object {
        fun create(names: List<String>, references: List<Pair<String, String>>): List<ScopedObservations> {
            return create(emptyList(), names, references)
        }

        private fun create(
            currentGroup: List<String>,
            unsortedNames: List<String>,
            references: List<Pair<String, String>>
        ): List<ScopedObservations> {
            val names = unsortedNames.sorted()
            val (subGroupReferences, groupReferences) = references.groupBy { (first, second) ->
                oneLevelDownFromGroup(currentGroup, first) to oneLevelDownFromGroup(currentGroup, second)
            }.toList().partition { it.first.first == it.first.second }
            val currentReferenceReasons = groupReferences.toMap()
            val currentScopedObservations = ScopedObservations(currentGroup, names, currentReferenceReasons)
            val subGroupObservationsList = subGroupReferences.flatMap { (identicalPair, allReasons) ->
                val subGroupPath = identicalPair.first.toCodeUnit().parts
                val target = CodeUnit(subGroupPath).toName()
                val reasons = allReasons.filterNot {
                    val firstName = it.first
                    val secondName = it.second
                    val booleanResult = firstName == target || secondName == target
                    booleanResult
                }
                val newNames = names.filter(startsWithGroup(subGroupPath))
                create(subGroupPath, newNames, reasons)
            }
            return listOf(currentScopedObservations) + subGroupObservationsList
        }

        private fun oneLevelDownFromGroup(groupPath: List<String>, name: String): String {
            val result = CodeUnit(name.toCodeUnit().parts.subList(0, groupPath.size + 1)).toName()
            return result
        }

        private fun startsWithGroup(groupPath: List<String>): (String) -> Boolean = { name ->
            val nameParts = name.toCodeUnit().parts
            if (nameParts.size < groupPath.size) {
                false
            } else if (nameParts.subList(0, groupPath.size) == groupPath) {
                true
            } else {
                false
            }
        }
    }
}
