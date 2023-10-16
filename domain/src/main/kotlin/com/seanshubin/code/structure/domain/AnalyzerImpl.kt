package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.binaryparser.BinaryDetail
import com.seanshubin.code.structure.cycle.CycleUtil
import com.seanshubin.code.structure.domain.Analysis.Companion.referenceComparator
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
        val detailsExceptTransitive = composeDetailsExceptTransitive(names, references, cycles)
        val details = composeDetails(names, detailsExceptTransitive)
        val errorDetail = if (newInCycle.isEmpty()) null else ErrorDetail(newInCycle.toList().sorted())
        return Analysis(observations, cycles, names, references, cycleDetails, details, errorDetail)
    }

    private fun composeDetails(
        names: List<String>,
        detailsExceptTransitive: Map<String, DetailExceptTransitive>
    ): Map<String, Detail> {
        return names.associateWith { composeDetail(it, detailsExceptTransitive) }
    }

    private fun composeDetail(name: String, partialDetails: Map<String, DetailExceptTransitive>): Detail {
        val partialDetail = partialDetails.getValue(name)
        val transitiveOut = followArrows(name, partialDetails) { detail: DetailExceptTransitive -> detail.arrowsOut }
        val transitiveIn = followArrows(name, partialDetails) { detail: DetailExceptTransitive -> detail.arrowsIn }
        return Detail(
            name = partialDetail.name,
            othersInSameCycle = partialDetail.othersInSameCycle,
            arrowsOut = partialDetail.arrowsOut,
            arrowsIn = partialDetail.arrowsIn,
            transitiveOut = transitiveOut,
            transitiveIn = transitiveIn,
        )
    }

    private fun followArrows(
        name: String,
        partialDetails: Map<String, DetailExceptTransitive>,
        lookupArrows: (DetailExceptTransitive) -> Arrows
    ): Set<String> {
        val partialDetail = partialDetails.getValue(name)
        val otherCycle = partialDetail.othersInSameCycle
        val cycleIncludingThis = otherCycle + name
        val immediate = cycleIncludingThis.flatMap { partOfCycle ->
            val currentDetail = partialDetails.getValue(partOfCycle)
            val notInCycle = lookupArrows(currentDetail).notInCycle
            notInCycle
        }
        val deep = immediate.flatMap{followArrows(it, partialDetails, lookupArrows)}.toSet()
        val transitive = otherCycle + immediate + deep
        return transitive
    }

    private fun composeDetailsExceptTransitive(
        names: List<String>,
        references: List<Pair<String, String>>,
        cycles: List<List<String>>
    ): Map<String, DetailExceptTransitive> {
        val referencesByFirst = references.groupBy { it.first }
        val referencesBySecond = references.groupBy { it.second }
        val allArrowsOut = names.associateWith { name ->
            val arrowsOut: List<String> = referencesByFirst[name]?.map { it.second } ?: emptyList()
            arrowsOut.toSet()
        }
        val allArrowsIn = names.associateWith { name ->
            val arrowsIn: List<String> = referencesBySecond[name]?.map { it.first } ?: emptyList()
            arrowsIn.toSet()
        }
        val existingCyclesByName = cycles.flatMap { cycle ->
            cycle.map { name ->
                name to cycle.toSet()
            }
        }.toMap()
        val allCyclesByName = names.associateWith { name ->
            existingCyclesByName[name] ?: emptySet()
        }
        return names.associateWith { name ->
            composeDetailExceptTransitive(name, allArrowsOut, allArrowsIn, allCyclesByName)
        }
    }

    private fun composeDetailExceptTransitive(
        name: String,
        allArrowsOut: Map<String, Set<String>>,
        allArrowsIn: Map<String, Set<String>>,
        allCyclesByName: Map<String, Set<String>>
    ): DetailExceptTransitive {
        val othersInSameCycle = (allCyclesByName[name]?.filterNot { it == name } ?: emptyList()).toSet()
        val cycle = othersInSameCycle + name
        val rawArrowsOut = allArrowsOut.getValue(name)
        val rawArrowsIn = allArrowsIn.getValue(name)
        val (arrowsOutCycle, arrowsOutNoCycle) = rawArrowsOut.partition { cycle.contains(it) }
        val arrowsOut = Arrows(arrowsOutCycle.toSet(), arrowsOutNoCycle.toSet())
        val (arrowsInCycle, arrowsInNoCycle) = rawArrowsIn.partition { cycle.contains(it) }
        val arrowsIn = Arrows(arrowsInCycle.toSet(), arrowsInNoCycle.toSet())
        return DetailExceptTransitive(
            name,
            othersInSameCycle,
            arrowsOut,
            arrowsIn
        )
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
        private fun findCycles(references: List<Pair<String, String>>): List<List<String>> {
            val edges = references.toSet()
            return CycleUtil.findCycles(edges)
        }
    }
}
