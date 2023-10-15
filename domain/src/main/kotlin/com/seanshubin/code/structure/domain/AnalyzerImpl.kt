package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.binaryparser.BinaryDetail
import com.seanshubin.code.structure.cycle.CycleUtil
import com.seanshubin.code.structure.utility.stateless.ListUtil

class AnalyzerImpl : Analyzer {
    override fun analyze(observations: Observations): Analysis {
        val rawNames = observations.binaries.map { it.name }
        val rawIds = rawNames.map { it.split('.') }
        val commonPrefix = ListUtil.commonPrefix(rawIds)
        val names = observations.binaries.map { it.toName(commonPrefix) }.sorted().distinct()
        val references = observations.binaries.flatMap { binary ->
            binary.dependencyNames.map {
                binary.toName(commonPrefix) to it.toName(commonPrefix)
            }
        }.sortedWith(referenceComparator).distinct()
        val cycles = findCycles(references)
        val oldInCycle = observations.oldInCycle.distinct().toSet()
        val currentInCycle = cycles.flatten().distinct().toSet()
        val newInCycle = currentInCycle - oldInCycle
        val cycleDetails = composeAllCycleDetails(cycles, references)
        val localDetails = composeLocalDetails(names, references)
        val errorDetail = if (newInCycle.isEmpty()) null else ErrorDetail(newInCycle.toList().sorted())
        return Analysis(observations, cycles, names, references, cycleDetails, localDetails, errorDetail)
    }

    private fun composeLocalDetails(
        names: List<String>,
        references: List<Pair<String, String>>
    ): Map<String, LocalDetail> =
        names.associate { composeLocalDetailPair(it, references) }

    private fun composeLocalDetailPair(
        name: String,
        allReferences: List<Pair<String, String>>
    ): Pair<String, LocalDetail> {
        val references = allReferences.filter { it.first == name || it.second == name }
        val nameWithDuplicates = listOf(name) + references.flatMap { it.toList() }
        val names = nameWithDuplicates.sorted().distinct()
        val localDetail = LocalDetail(name, names, references)
        return name to localDetail
    }

    private fun composeAllCycleDetails(
        cycles: List<List<String>>,
        references: List<Pair<String, String>>
    ): List<CycleDetail> =
        cycles.map { composeSingleCycleDetails(it, references) }

    private fun composeSingleCycleDetails(cycle: List<String>, allReferences: List<Pair<String, String>>): CycleDetail {
        val references = allReferences.filter { cycle.contains(it.first) && cycle.contains(it.second) }
        return CycleDetail(cycle, references)
    }


    private fun BinaryDetail.toName(commonPrefix: List<String>): String = this.name.toName(commonPrefix)

    private fun String.toName(commonPrefix: List<String>): String {
        val parts = this.split('.')
        val commonPrefixSize = commonPrefix.size
        val prefix = parts.take(commonPrefixSize)
        if (prefix != commonPrefix) {
            throw RuntimeException("Expected $this to start with $commonPrefix")
        }
        val remain = parts.drop(commonPrefixSize)
        return remain.joinToString(".")
    }

    companion object {
        private val referenceComparator = Comparator
            .comparing<Pair<String, String>, String> { it.first }
            .thenComparing(Comparator.comparing { it.second })

        private fun findCycles(references: List<Pair<String, String>>): List<List<String>> {
            val edges = references.toSet()
            return CycleUtil.findCycles(edges)
        }
    }
}
