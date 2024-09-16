package com.seanshubin.code.structure.collection

object ListUtil {
    val listComparator: Comparator<List<String>> = Comparator { o1, o2 ->
        val sizeCompare = o1.size.compareTo(o2.size)
        if (sizeCompare != 0) return@Comparator sizeCompare
        val size = o1.size
        for (i in 0 until size) {
            val comparison = o1[i].compareTo(o2[i])
            if (comparison != 0) return@Comparator comparison
        }
        0
    }

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