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
        return analyze(names, references)
    }

    companion object {
        private val listSizeComparator = Comparator<List<String>> { o1, o2 -> o1.size.compareTo(o2.size) }
        private val firstInListComparator = Comparator<List<String>> { o1, o2 -> o1[0].compareTo(o2[0]) }
        private val sizeThenFirstComparator = listSizeComparator.reversed().then(firstInListComparator)

        private fun analyze(names: List<String>, references: List<Pair<String, String>>): Analysis {
            val cycles = findCycles(references)
            val entryPoints = findEntryPoints(names, references)
            val cycleDetails = composeAllCycleDetails(cycles, references)
            val details = composeDetails(names, references, cycles)
            val groups = composeGroups(names, references)
            return Analysis(
                cycles,
                names,
                references,
                entryPoints,
                cycleDetails,
                details,
                groups
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

        private fun composeGroups(names: List<String>, references: List<Pair<String, String>>):Map<List<String>, Analysis>{
//            val flatNames = flattenNames(names)
//            val flatReferences = flattenReferences(references)
//            val path = emptyList<String>()
//            val analysis = analyze(flatNames, flatReferences)
//            return mapOf(path to analysis)
            return emptyMap()
        }

        private fun flattenNames(names:List<String>):List<String> =
            names.map(::flattenName).distinct()

        private fun flattenName(name:String):String {
            val dotIndex = name.indexOf('.')
            return if(dotIndex == -1){
                name
            } else {
                name.substring(0, dotIndex)
            }
        }

        private fun flattenReferences(references: List<Pair<String, String>>):List<Pair<String, String>> =
            references.map(::flattenReference).distinct()

        private fun flattenReference(reference:Pair<String, String>):Pair<String, String> =
            flattenName(reference.first) to flattenName(reference.second)

    }
}
