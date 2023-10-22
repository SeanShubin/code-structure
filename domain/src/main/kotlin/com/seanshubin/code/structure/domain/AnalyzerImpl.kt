package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.binaryparser.BinaryDetail
import com.seanshubin.code.structure.collection.ListUtil
import com.seanshubin.code.structure.cycle.CycleUtil
import com.seanshubin.code.structure.domain.Name.groupToName
import com.seanshubin.code.structure.domain.Name.isAncestorOf
import com.seanshubin.code.structure.domain.ScopedAnalysis.Companion.referenceComparator

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
        val global = analyze(names, references)
        val ancestorToDescendant = references.filter {
            it.first.isAncestorOf(it.second)
        }
        val descendantToAncestor = references.filter {
            it.second.isAncestorOf(it.first)
        }
        val uriByName = composeUriByName(observations, commonPrefix)
        val lineage = Lineage(ancestorToDescendant, descendantToAncestor)
        val byGroup = composeGroups(emptyList(), NamesReferences(names, references)).toMap()
        val errors = composeErrors(global, byGroup, lineage)
        val summary = composeSummary(global, byGroup, ancestorToDescendant, descendantToAncestor)
        return Analysis(global, uriByName, lineage, byGroup, errors, summary)
    }

    companion object {
        private val listSizeComparator = Comparator<List<String>> { o1, o2 -> o1.size.compareTo(o2.size) }
        private val firstInListComparator = Comparator<List<String>> { o1, o2 -> o1[0].compareTo(o2[0]) }
        private val sizeThenFirstComparator = listSizeComparator.reversed().then(firstInListComparator)

        private fun composeSummary(
            global: ScopedAnalysis,
            byGroup: Map<List<String>, ScopedAnalysis>,
            ancestorToDescendant: List<Pair<String, String>>,
            descendantToAncestor: List<Pair<String, String>>
        ): Summary {
            val inCycleCount = global.cycles.sumOf { it.size }
            val inGroupCycleCount = byGroup.values.sumOf { groupCycles ->
                groupCycles.cycles.sumOf { cycles -> cycles.size }
            }
            val ancestorDependsOnDescendantCount = ancestorToDescendant.size
            val descendantDependsOnAncestorCount = descendantToAncestor.size
            return Summary(
                inCycleCount,
                inGroupCycleCount,
                ancestorDependsOnDescendantCount,
                descendantDependsOnAncestorCount
            )
        }

        private fun composeErrors(
            global: ScopedAnalysis,
            byGroup: Map<List<String>, ScopedAnalysis>,
            lineage: Lineage
        ): Errors {
            val directCycles = global.cycles.flatten().distinct().sorted()
            val groupCycles = byGroup.flatMap { (group, scopedAnalysis) ->
                scopedAnalysis.cycles.flatten().map { group.groupToName(it) }
            }.distinct().sorted()
            val ancestorDependsOnDescendant = lineage.ancestorDependsOnDescendant
            val descendantDependsOnAncestor = lineage.descendantDependsOnAncestor
            return Errors(directCycles, groupCycles, ancestorDependsOnDescendant, descendantDependsOnAncestor)
        }

        private fun composeUriByName(observations: Observations, commonPrefix: List<String>): Map<String, String> {
            return observations.sources.flatMap { sourceDetail ->
                sourceDetail.modules.map { rawName ->
                    val name = rawName.toName(commonPrefix)
                    val link = observations.sourcePrefix + sourceDetail.path
                    name to link
                }
            }.toMap()
        }

        private fun analyze(names: List<String>, references: List<Pair<String, String>>): ScopedAnalysis {
            val cycles = findCycles(references)
            val entryPoints = findEntryPoints(names, references)
            val cycleDetails = composeAllCycleDetails(cycles, references)
            val details = composeDetails(names, references, cycles)
            return ScopedAnalysis(
                cycles,
                names,
                references,
                entryPoints,
                cycleDetails,
                details
            )
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

        private fun findCycles(references: List<Pair<String, String>>): List<List<String>> {
            val edges = references.toSet()
            val cycles = CycleUtil.findCycles(edges)
            return cycles.map { it.sorted() }.sortedWith(sizeThenFirstComparator)
        }

        private fun findEntryPoints(names: List<String>, references: List<Pair<String, String>>): List<String> {
            return names.filterNot { name ->
                references.map { it.second }.contains(name)
            }
        }

        private fun composeAllCycleDetails(
            cycles: List<List<String>>,
            references: List<Pair<String, String>>
        ): List<CycleDetail> =
            cycles.map { composeSingleCycleDetails(it, references) }

        private fun composeSingleCycleDetails(
            cycle: List<String>,
            allReferences: List<Pair<String, String>>
        ): CycleDetail {
            val references = allReferences.filter { cycle.contains(it.first) && cycle.contains(it.second) }
            return CycleDetail(cycle, references)
        }

        fun composeDetails(
            names: List<String>,
            references: List<Pair<String, String>>,
            cycles: List<List<String>>
        ): Map<String, Detail> {
            val referencesByFirst = references.groupBy { it.first }
            val referencesBySecond = references.groupBy { it.second }
            val referencesOutByName = names.associateWith { name ->
                val referencesOut: List<String> = referencesByFirst[name]?.map { it.second } ?: emptyList()
                referencesOut.toSet()
            }
            val referencesInByName = names.associateWith { name ->
                val referencesIn: List<String> = referencesBySecond[name]?.map { it.first } ?: emptyList()
                referencesIn.toSet()
            }
            val cyclesByName = cycles.flatMap { cycle ->
                cycle.map { name ->
                    name to cycle.toSet()
                }
            }.toMap()
            return names.associateWith { name ->
                composeDetail(name, referencesOutByName, referencesInByName, cyclesByName)
            }
        }

        private fun composeDetail(
            name: String,
            referencesOutByName: Map<String, Set<String>>,
            referencesInByName: Map<String, Set<String>>,
            cyclesByName: Map<String, Set<String>>
        ): Detail {
            val cycle = cyclesByName[name]
            val directionOut = composeDirectionalArrow(name, referencesOutByName, cyclesByName)
            val directionIn = composeDirectionalArrow(name, referencesInByName, cyclesByName)
            val arrows = Arrows(directionOut, directionIn)
            return Detail(
                name,
                cycle,
                arrows
            )
        }

        private fun composeDirectionalArrow(
            name: String,
            referencesByName: Map<String, Set<String>>,
            cyclesByName: Map<String, Set<String>>
        ): DirectionalArrow {
            val references = referencesByName.getValue(name)
            val cycle = cyclesByName[name] ?: emptySet()
            val (inCycle, notInCycle) = references.partition { cycle.contains(it) }
            val transitive = findTransitive(name, referencesByName, cyclesByName)
            return DirectionalArrow(
                inCycle.toSet(),
                notInCycle.toSet(),
                transitive
            )
        }

        private fun findTransitive(
            name: String,
            referencesByName: Map<String, Set<String>>,
            cyclesByName: Map<String, Set<String>>
        ): Set<String> {
            val thisOrCycle = cyclesByName[name] ?: setOf(name)
            val immediate = thisOrCycle.flatMap { partOfCycle ->
                referencesByName.getValue(partOfCycle)
            }.filterNot {
                thisOrCycle.contains(it)
            }
            val deep = immediate.flatMap {
                findTransitive(it, referencesByName, cyclesByName)
            }.toSet()
            val transitive = (thisOrCycle + immediate + deep) - name
            return transitive
        }

        private fun composeGroups(
            path: List<String>,
            namesReferences: NamesReferences
        ): List<Pair<List<String>, ScopedAnalysis>> {
            if (namesReferences.names.isEmpty()) return emptyList()
            val top = namesReferences.head()
            val topAnalysis = analyze(top.names, top.references)
            val topEntry = path to topAnalysis
            val descendantMap = top.names.flatMap {
                val childPath = path + it
                val childNamesReferences = namesReferences.tail(it)
                composeGroups(childPath, childNamesReferences)
            }
            return listOf(topEntry) + descendantMap
        }

    }
}
