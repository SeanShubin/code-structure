package com.seanshubin.code.structure.relationparser

data class RelationDetail(
    val source: SourceLocation,
    val name: String,
    val dependencyNames: List<String>
) : Comparable<RelationDetail> {
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
