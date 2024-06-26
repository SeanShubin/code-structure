package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ComparatorUtil.pairComparator
import com.seanshubin.code.structure.collection.ListUtil
import com.seanshubin.code.structure.cycle.CycleUtil
import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.relationparser.RelationDetail

class AnalyzerImpl(
    private val timer: Timer,
    private val cycleLoopEvent: (String, Int, Int) -> Unit
) : Analyzer {
    override fun analyze(observations: Observations): Analysis {
        val qualifiedNames = observations.sources.flatMap { it.modules }.sorted().distinct()
        val qualifiedIds = qualifiedNames.map { it.toCodeUnit().parts }
        val commonPrefix = ListUtil.commonPrefix(qualifiedIds)
        val names = timer.monitor("analysis.names") {
            qualifiedNames.map { it.toName(commonPrefix) }
        }
        val references = timer.monitor("analysis.references") {
            observations.binaries.flatMap { binary ->
                binary.dependencyNames.map { dependency ->
                    binary.name to dependency
                }
            }
                .sortedWith(pairComparator)
                .filter(bothPartsOfReferenceInList(qualifiedNames))
                .map { (first, second) ->
                    first.toName(commonPrefix) to second.toName(commonPrefix)
                }
                .distinct()
        }
        val cycleLoop = cycleLoopFunction("analysis.global.cycle")
        val global = timer.monitor("analysis.global") { analyze(names, references, cycleLoop) }
        val ancestorToDescendant = timer.monitor("analysis.ancestorToDescendant") {
            references.filter {
                it.first.toCodeUnit().isAncestorOf(it.second.toCodeUnit())
            }
        }
        val descendantToAncestor = timer.monitor("analysis.descendantToAncestor") {
            references.filter {
                it.second.toCodeUnit().isAncestorOf(it.first.toCodeUnit())
            }
        }
        val nameUriList = timer.monitor("analysis.nameUriList") { composeNameUriList(observations, commonPrefix) }
        val lineage = timer.monitor("analysis.lineage") { Lineage(ancestorToDescendant, descendantToAncestor) }
        val groupScopedAnalysisList = timer.monitor("analysis.groupScopedAnalysisList") {
            composeGroupScopedAnalysisList(
                emptyList(),
                NamesReferences(names, references),
                CycleUtil.cycleLoopNop
            )
        }
        val errors = timer.monitor("analysis.errors") { composeErrors(global, groupScopedAnalysisList, lineage) }
        val summary = timer.monitor("analysis.summary") {
            composeSummary(
                global,
                groupScopedAnalysisList,
                ancestorToDescendant,
                descendantToAncestor
            )
        }
        return Analysis(global, nameUriList, lineage, groupScopedAnalysisList, errors, summary)
    }

    private fun bothPartsOfReferenceInList(list: List<String>): (Pair<String, String>) -> Boolean = { reference ->
        list.contains(reference.first) && list.contains(reference.second)
    }

    private fun cycleLoopFunction(caption: String): (Int, Int) -> Unit = { index, size ->
        cycleLoopEvent(caption, index, size)
    }

    companion object {
        private val listSizeComparator = Comparator<List<String>> { o1, o2 -> o1.size.compareTo(o2.size) }
        private val firstInListComparator = Comparator<List<String>> { o1, o2 -> o1[0].compareTo(o2[0]) }
        private val sizeThenFirstComparator = listSizeComparator.reversed().then(firstInListComparator)

        private fun composeSummary(
            global: ScopedAnalysis,
            groupScopedAnalysisList: List<Pair<List<String>, ScopedAnalysis>>,
            ancestorToDescendant: List<Pair<String, String>>,
            descendantToAncestor: List<Pair<String, String>>
        ): Summary {
            val inCycleCount = global.cycles.sumOf { it.size }
            val inGroupCycleCount = groupScopedAnalysisList.map { it.second }.sumOf { scopedAnalysis ->
                scopedAnalysis.cycles.sumOf { cycles -> cycles.size }
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
            groupScopedAnalysisList: List<Pair<List<String>, ScopedAnalysis>>,
            lineage: Lineage
        ): Errors {
            val inDirectCycle = global.cycles.flatten().distinct().sorted()
            val inGroupCycle = groupScopedAnalysisList.flatMap { (group, scopedAnalysis) ->
                scopedAnalysis.cycles.flatten().map { CodeUnit(group).resolve(it).toName() }
            }.distinct().sorted()
            return Errors(inDirectCycle, inGroupCycle, lineage)
        }

        private fun composeNameUriList(
            observations: Observations,
            commonPrefix: List<String>
        ): List<Pair<String, String>> {
            return observations.sources.flatMap { sourceDetail ->
                sourceDetail.modules.map { rawName ->
                    val name = rawName.toName(commonPrefix)
                    val link = observations.sourcePrefix + sourceDetail.path
                    name to link
                }
            }
        }

        private fun analyze(
            names: List<String>,
            references: List<Pair<String, String>>,
            cycleLoop: (Int, Int) -> Unit
        ): ScopedAnalysis {
            val cycles = findCycles(references, cycleLoop)
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

        private fun RelationDetail.toName(commonPrefix: List<String>): String = this.name.toName(commonPrefix)

        private fun String.toName(commonPrefix: List<String>): String {
            val parts = this.toCodeUnit().parts
            val commonPrefixSize = commonPrefix.size
            val prefix = parts.take(commonPrefixSize)
            if (prefix != commonPrefix) {
                throw RuntimeException("Expected $this to start with $commonPrefix")
            }
            val remain = parts.drop(commonPrefixSize)
            return CodeUnit(remain).toName()
        }

        private fun findCycles(references: List<Pair<String, String>>, loop: (Int, Int) -> Unit): List<List<String>> {
            val edges = references.toSet()
            val cycles = CycleUtil.findCycles(edges, loop)
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
        ): List<Detail> {
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
            return names.map { name ->
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
                if (referencesByName.containsKey(partOfCycle)) {
                    referencesByName.getValue(partOfCycle)
                } else {
                    throw RuntimeException(partOfCycle)
                }
            }.filterNot {
                thisOrCycle.contains(it)
            }
            val deep = immediate.flatMap {
                findTransitive(it, referencesByName, cyclesByName)
            }.toSet()
            val transitive = (thisOrCycle + immediate + deep) - name
            return transitive
        }

        private fun composeGroupScopedAnalysisList(
            path: List<String>,
            namesReferences: NamesReferences,
            cycleLoop: (Int, Int) -> Unit
        ): List<Pair<List<String>, ScopedAnalysis>> {
            if (namesReferences.names.isEmpty()) return emptyList()
            val top = namesReferences.head()
            val topAnalysis = analyze(top.names, top.references, cycleLoop)
            val topEntry = path to topAnalysis
            val descendantMap = top.names.flatMap {
                val childPath = path + it
                val childNamesReferences = namesReferences.tail(it)
                composeGroupScopedAnalysisList(childPath, childNamesReferences, cycleLoop)
            }
            return listOf(topEntry) + descendantMap
        }

    }
}
