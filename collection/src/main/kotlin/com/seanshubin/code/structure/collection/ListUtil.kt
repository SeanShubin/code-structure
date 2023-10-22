package com.seanshubin.code.structure.collection

object ListUtil {
    fun <T> List<T>.startsWith(prefix: List<T>): Boolean {
        if (prefix.size > size) return false
        return take(prefix.size) == prefix
    }

    fun commonPrefix(list: List<List<String>>): List<String> {
        if (list.isEmpty()) return emptyList()
        var candidate = list[0]
        if (candidate.isEmpty()) return candidate
        var currentIndex = 1
        while (currentIndex < list.size) {
            val current = list[currentIndex]
            if (current.isEmpty()) return emptyList()
            if (current.size < candidate.size) {
                candidate = candidate.take(current.size)
            }
            if (current.take(candidate.size) == candidate) {
                currentIndex++
                continue
            }
            candidate = candidate.take(candidate.size - 1)
            if (candidate.isEmpty()) return candidate
        }
        return candidate
    }
}