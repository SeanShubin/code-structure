package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit

data class ScopedObservations(
    val groupPath: List<String>,
    val names: List<String>,
    val referenceReasons: Map<Pair<String, String>, List<Pair<String, String>>>
) {
    fun unqualifiedNames(): List<String> {
        return names.map { unqualify(groupPath, it) }.distinct()
    }

    fun unqualifiedReferenceQualifiedReasons(): Map<Pair<String, String>, List<Pair<String, String>>> {
        return referenceReasons.map { (reference, reasons) ->
            val (first, second) = reference
            val unqualifiedReference = unqualify(groupPath, first) to unqualify(groupPath, second)
            unqualifiedReference to reasons
        }.toMap()
    }

    companion object {
        fun create(names: List<String>, references: List<Pair<String, String>>): List<ScopedObservations> {
            return create(emptyList(), names, references)
        }

        private fun create(
            currentGroup: List<String>,
            names: List<String>,
            references: List<Pair<String, String>>
        ): List<ScopedObservations> {
            val subGroups = names.mapNotNull { subGroup(currentGroup, it) }.distinct()
            val (subGroupReferences, groupReferences) = references.groupBy { (first, second) ->
                oneLevelDownFromGroup(currentGroup, first) to oneLevelDownFromGroup(currentGroup, second)
            }.toList().partition { it.first.first == it.first.second }
            val currentReferenceReasons = groupReferences.toMap()
            val currentScopedObservations = ScopedObservations(currentGroup, names, currentReferenceReasons)
            val subGroupReferenceMap = subGroupReferences.toMap()
            val subGroupObservationsList = subGroups.flatMap { subGroup ->
                val subGroupPath = subGroup.toCodeUnit().parts
                val newNames = names.filter(startsWithGroup(subGroupPath))
                if (newNames.isEmpty()) {
                    emptyList()
                } else {
                    val key = subGroup to subGroup
                    val allReasons = subGroupReferenceMap[key] ?: emptyList()
                    val target = CodeUnit(subGroupPath).toName()
                    val reasons = allReasons.filterNot {
                        val firstName = it.first
                        val secondName = it.second
                        val booleanResult = firstName == target || secondName == target
                        booleanResult
                    }
                    create(subGroupPath, newNames, reasons)
                }
            }
            return listOf(currentScopedObservations) + subGroupObservationsList
        }

        private fun subGroup(groupPath: List<String>, name: String): String? {
            val nameParts = name.toCodeUnit().parts
            return if (nameParts.size > groupPath.size) {
                CodeUnit(nameParts.subList(0, groupPath.size + 1)).toName()
            } else {
                null
            }
        }

        private fun oneLevelDownFromGroup(groupPath: List<String>, name: String): String {
            val result = CodeUnit(name.toCodeUnit().parts.subList(0, groupPath.size + 1)).toName()
            return result
        }

        private fun startsWithGroup(groupPath: List<String>): (String) -> Boolean = { name ->
            val nameParts = name.toCodeUnit().parts
            if (nameParts.size <= groupPath.size) {
                false
            } else if (nameParts.subList(0, groupPath.size) == groupPath) {
                true
            } else {
                false
            }
        }

        fun unqualify(groupPath: List<String>, name: String): String {
            val nameParts = name.toCodeUnit().parts
            return nameParts[groupPath.size]
        }
    }
}
