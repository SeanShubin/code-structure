package com.seanshubin.code.structure.domain

data class NamesReferences(
    val names: List<String>,
    val references: List<Pair<String, String>>
) {
    fun head(): NamesReferences {
        val newNames = names.map { topOnly(it) }.distinct()
        val newReferences = references.map {
            topOnly(it.first) to topOnly(it.second)
        }.distinct().filterNot { it.first == it.second }
        return NamesReferences(newNames, newReferences)
    }

    fun tail(top: String): NamesReferences {
        val newNames = names.mapNotNull { remainOnly(it, top) }
        val newReferences = references.mapNotNull { remainOnlyReference(it, top) }
        return NamesReferences(newNames, newReferences)
    }

    fun topOnly(name: String): String = name.split('.')[0]
    fun remainOnly(name: String, top: String): String? {
        val parts = name.split('.')
        if (parts[0] != top) return null
        val tail = parts.drop(1)
        if (tail.isEmpty()) return null
        return tail.joinToString(".")
    }

    fun remainOnlyReference(reference: Pair<String, String>, top: String): Pair<String, String>? {
        val remainOnlyFirst = remainOnly(reference.first, top) ?: return null
        val remainOnlySecond = remainOnly(reference.second, top) ?: return null
        return remainOnlyFirst to remainOnlySecond
    }
}
