package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ComparatorUtil.comparingFirst
import com.seanshubin.code.structure.collection.ComparatorUtil.pairComparator
import com.seanshubin.code.structure.collection.ListUtil.listComparator

object ReportSorting {
    fun Set<String>.sortedForReport(): List<String> = sorted()
    fun Set<Pair<String, String>>.sortedPairsForReport(): List<Pair<String, String>> = sortedWith(pairComparator)
    fun Set<Set<String>>.sortedSetOfSetOfStringForReport(): List<List<String>> =
        map { it.sortedForReport() }.sortedWith(listComparator)

    fun Map<List<String>, ScopedAnalysis>.sortedForReport(): List<Pair<List<String>, ScopedAnalysis>> =
        toList().sortedWith(comparingFirst(listComparator))
}
