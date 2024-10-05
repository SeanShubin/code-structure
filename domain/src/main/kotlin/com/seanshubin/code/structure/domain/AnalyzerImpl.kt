package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ComparatorUtil.pairComparator
import com.seanshubin.code.structure.collection.ListUtil
import com.seanshubin.code.structure.cycle.CycleAlgorithm
import com.seanshubin.code.structure.domain.CodeUnit.Companion.toCodeUnit
import com.seanshubin.code.structure.relationparser.RelationDetail

class AnalyzerImpl(
    private val timer: Timer,
    private val cycleAlgorithm: CycleAlgorithm,
    private val countAsErrors: CountAsErrors,
    private val errorLimit: Int
) : Analyzer {
    private val sourceName = "analyzer"
    override fun analyze(observations: Observations): Analysis {
        val qualifiedNames = observations.sources.flatMap { it.modules }.sorted().distinct()
        val qualifiedIds = qualifiedNames.map { it.toCodeUnit().parts }
        val commonPrefix = ListUtil.commonPrefix(qualifiedIds)
        val names = timer.monitor(sourceName, "analysis.names") {
            qualifiedNames.map { it.toName(commonPrefix) }
        }
        val references = timer.monitor(sourceName, "analysis.references") {
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
        val globalReferenceReasons = references.map { it to emptyList<Pair<String, String>>() }.toMap()
        val global = timer.monitor(sourceName, "analysis.global") {
            analyze(
                sourceName,
                names,
                globalReferenceReasons,
                cycleAlgorithm,
                timer
            )
        }
        val ancestorToDescendant = timer.monitor(sourceName, "analysis.ancestorToDescendant") {
            references.filter {
                it.first.toCodeUnit().isAncestorOf(it.second.toCodeUnit())
            }
        }
        val descendantToAncestor = timer.monitor(sourceName, "analysis.descendantToAncestor") {
            references.filter {
                it.second.toCodeUnit().isAncestorOf(it.first.toCodeUnit())
            }
        }
        val nameUriList =
            timer.monitor(sourceName, "analysis.nameUriList") { composeNameUriList(observations, commonPrefix) }
        val lineage =
            timer.monitor(sourceName, "analysis.lineage") { Lineage(ancestorToDescendant, descendantToAncestor) }
        val groupScopedAnalysisList = timer.monitor(sourceName, "analysis.groupScopedAnalysisList") {
            val scopedObservationsList = ScopedObservations.create(names, references)
            scopedObservationsList.map {
                composeGroupScopedAnalysisList(
                    sourceName,
                    it,
                    cycleAlgorithm,
                    timer
                )
            }
        }
        val summary = timer.monitor(sourceName, "analysis.summary") {
            composeSummary(
                countAsErrors,
                errorLimit,
                global,
                groupScopedAnalysisList,
                ancestorToDescendant,
                descendantToAncestor
            )
        }
        return Analysis(global, nameUriList, lineage, groupScopedAnalysisList, summary)
    }

    private fun bothPartsOfReferenceInList(list: List<String>): (Pair<String, String>) -> Boolean = { reference ->
        list.contains(reference.first) && list.contains(reference.second)
    }

    companion object {
        private val listSizeComparator = Comparator<List<String>> { o1, o2 -> o1.size.compareTo(o2.size) }
        private val firstInListComparator = Comparator<List<String>> { o1, o2 -> o1[0].compareTo(o2[0]) }
        private val sizeThenFirstComparator = listSizeComparator.reversed().then(firstInListComparator)

        private fun composeSummary(
            countAsErrors: CountAsErrors,
            errorLimit: Int,
            global: ScopedAnalysis,
            groupScopedAnalysisList: List<Pair<List<String>, ScopedAnalysis>>,
            ancestorToDescendant: List<Pair<String, String>>,
            descendantToAncestor: List<Pair<String, String>>
        ): Summary {
            val directCycleCount = global.cycles.sumOf { it.size }
            val inGroupCycleCount = groupScopedAnalysisList.map { it.second }.sumOf { scopedAnalysis ->
                scopedAnalysis.cycles.sumOf { cycles -> cycles.size }
            }
            val ancestorDependsOnDescendantCount = ancestorToDescendant.size
            val descendantDependsOnAncestorCount = descendantToAncestor.size
            return Summary(
                mapOf(
                    ErrorType.DIRECT_CYCLE to ErrorSummaryItem(directCycleCount, countAsErrors.directCycle),
                    ErrorType.GROUP_CYCLE to ErrorSummaryItem(inGroupCycleCount, countAsErrors.groupCycle),
                    ErrorType.ANCESTOR_DEPENDS_ON_DESCENDANT to ErrorSummaryItem(
                        ancestorDependsOnDescendantCount,
                        countAsErrors.ancestorDependsOnDescendant
                    ),
                    ErrorType.DESCENDANT_DEPENDS_ON_ANCESTOR to
                            ErrorSummaryItem(
                                descendantDependsOnAncestorCount,
                                countAsErrors.descendantDependsOnAncestor
                            ),
                ),
                errorLimit
            )
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
            sourceName: String,
            names: List<String>,
            referenceReasons: Map<Pair<String, String>, List<Pair<String, String>>>,
            cycleAlgorithm: CycleAlgorithm,
            timer: Timer
        ): ScopedAnalysis {
            val references = referenceReasons.keys.toList()
            val cycles = timer.monitor(sourceName, "analyze.cycles") { findCycles(references, cycleAlgorithm) }
            val entryPoints = timer.monitor(sourceName, "analyze.entryPoints") { findEntryPoints(names, references) }
            val cycleDetails =
                timer.monitor(sourceName, "analyze.cycleDetails") { composeAllCycleDetails(cycles, references) }
            val details = timer.monitor(sourceName, "analyze.details") { composeDetails(names, references, cycles) }
            return ScopedAnalysis(
                cycles,
                names,
                referenceReasons,
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
                throw RuntimeException("Expected '$this' to start with '$commonPrefix'")
            }
            val remain = parts.drop(commonPrefixSize)
            return CodeUnit(remain).toName()
        }

        private fun findCycles(
            references: List<Pair<String, String>>,
            cycleAlgorithm: CycleAlgorithm
        ): List<List<String>> {
            val edges = references.toSet()
            val cycles = cycleAlgorithm.findCycles(edges)
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
            val directionOut = referencesOutByName.getValue(name)
            val directionIn = referencesInByName.getValue(name)
            val arrows = Arrows(directionOut, directionIn)
            return Detail(
                name,
                cycle,
                arrows
            )
        }

        private fun composeGroupScopedAnalysisList(
            sourceName: String,
            scopedObservations: ScopedObservations,
            cycleAlgorithm: CycleAlgorithm,
            timer: Timer
        ): Pair<List<String>, ScopedAnalysis> {
            val names = scopedObservations.unqualifiedNames()
            val referenceReasons = scopedObservations.unqualifiedReferenceQualifiedReasons()
            val topAnalysis = analyze(sourceName, names, referenceReasons, cycleAlgorithm, timer)
            val topEntry = scopedObservations.groupPath to topAnalysis
            return topEntry
        }
    }
}
