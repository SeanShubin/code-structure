package com.seanshubin.code.structure.collection

object ComparatorUtil {
    val stringComparator: Comparator<String> = Comparator { o1, o2 -> o1.compareTo(o2) }
    val firstComparator = Comparator<Pair<String, String>> { o1, o2 -> o1.first.compareTo(o2.first) }
    val secondComparator = Comparator<Pair<String, String>> { o1, o2 -> o1.second.compareTo(o2.second) }
    val pairComparator = firstComparator.thenComparing(secondComparator)
}
