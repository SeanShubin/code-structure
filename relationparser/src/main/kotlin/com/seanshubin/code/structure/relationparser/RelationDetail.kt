package com.seanshubin.code.structure.relationparser

import java.nio.file.Path

data class RelationDetail(
    val file: Path,
    val pathInFile: String,
    val name: String,
    val dependencyNames: List<String>
) : Comparable<RelationDetail> {
    val location: String = if (pathInFile == "") {
        file.toString()
    } else {
        "$file!$pathInFile"
    }

    override fun compareTo(other: RelationDetail): Int =
        this.name.compareTo(other.name)

    companion object {
        fun List<RelationDetail>.toRelations(): List<Pair<String, String>> =
            flatMap { relationDetail ->
                relationDetail.dependencyNames.map { dependencyName ->
                    relationDetail.name to dependencyName
                }
            }
    }
}
