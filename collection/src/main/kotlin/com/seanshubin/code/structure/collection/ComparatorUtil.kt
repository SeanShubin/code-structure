package com.seanshubin.code.structure.collection

object ComparatorUtil {
    val stringComparator: Comparator<String> = Comparator { o1, o2 -> o1.compareTo(o2) }
    val firstComparator = Comparator<Pair<String, String>> { o1, o2 -> o1.first.compareTo(o2.first) }
    val secondComparator = Comparator<Pair<String, String>> { o1, o2 -> o1.second.compareTo(o2.second) }
    val pairComparator = firstComparator.thenComparing(secondComparator)
    fun <T, U> comparingFirst(comparator: Comparator<T>): Comparator<Pair<T, U>> =
        Comparator { o1, o2 -> comparator.compare(o1.first, o2.first) }

    fun <T, U> comparingSecond(comparator: Comparator<U>): Comparator<Pair<T, U>> =
        Comparator { o1, o2 -> comparator.compare(o1.second, o2.second) }
}
