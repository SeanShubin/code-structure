package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.binaryparser.BinaryDetail
import com.seanshubin.code.structure.cycle.CycleUtil
import com.seanshubin.code.structure.utility.stateless.ListUtil

class AnalyzerImpl : Analyzer {
    override fun analyze(observations: Observations): Analysis {
        val cycles = findCycles(observations)
        val rawNames = observations.binaries.map { it.name }
        val rawIds = rawNames.map{it.split('.')}
        val commonPrefix = ListUtil.commonPrefix(rawIds)
        val names = observations.binaries.map { it.toName(commonPrefix) }.sorted()
        val references = observations.binaries.flatMap { binary ->
            binary.dependencyNames.map {
                binary.toName(commonPrefix) to it.toName(commonPrefix)
            }
        }.sortedWith(referenceComparator)
        return Analysis(observations, cycles, names, references)
    }

    private fun BinaryDetail.toName(commonPrefix:List<String>):String = this.name.toName(commonPrefix)

    private fun String.toName(commonPrefix:List<String>):String {
        val parts = this.split('.')
        val commonPrefixSize = commonPrefix.size
        val prefix = parts.take(commonPrefixSize)
        if(prefix != commonPrefix){
            throw RuntimeException("Expected $this to start with $commonPrefix")
        }
        val remain = parts.drop(commonPrefixSize)
        return remain.joinToString(".")
    }

    companion object {
        private val referenceComparator = Comparator
            .comparing<Pair<String, String>, String> { it.first }
            .thenComparing(Comparator.comparing { it.second })

        private fun findCycles(observations: Observations): List<List<String>> {
            val edges = observations.binaries.flatMap { binary ->
                binary.dependencyNames.map { dependency ->
                    binary.name to dependency
                }
            }.toSet()
            return CycleUtil.findCycles(edges)
        }
    }
}
