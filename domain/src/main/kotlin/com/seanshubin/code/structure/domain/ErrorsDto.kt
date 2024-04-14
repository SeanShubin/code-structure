package com.seanshubin.code.structure.domain

import com.fasterxml.jackson.module.kotlin.readValue
import com.seanshubin.code.structure.json.JsonMappers

data class ErrorsDto(
    val directCycles: List<String>,
    val groupCycles: List<String>,
    val ancestorDependsOnDescendant: List<List<String>>,
    val descendantDependsOnAncestor: List<List<String>>
) {
    fun toDomain(): Errors = Errors(
        directCycles,
        groupCycles,
        Lineage(
            ancestorDependsOnDescendant.map { it[0] to it[1] },
            descendantDependsOnAncestor.map { it[0] to it[1] }
        )
    )

    fun toJson(): String = JsonMappers.pretty.writeValueAsString(this)

    companion object {
        fun String.jsonToErrors(): Errors = jsonToErrorsDto().toDomain()
        fun String.jsonToErrorsDto(): ErrorsDto = JsonMappers.parser.readValue(this)
        fun Errors.toDto(): ErrorsDto = ErrorsDto(
            inDirectCycle,
            inGroupCycle,
            lineage.ancestorDependsOnDescendant.map { it.toList() },
            lineage.descendantDependsOnAncestor.map { it.toList() }
        )

        fun Errors.toJson(): String = toDto().toJson()
    }
}
