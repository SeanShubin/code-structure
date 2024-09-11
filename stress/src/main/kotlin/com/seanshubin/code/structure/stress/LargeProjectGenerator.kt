package com.seanshubin.code.structure.stress

import kotlin.random.Random

class LargeProjectGenerator(
    val depth: Int,
    breadth: Int,
    alphabet: List<String>,
    val random: Random
) {
    private val backwardsRelationsCount = depth * breadth
    private val words = alphabet.take(breadth)
    private val multiplier = words.map { listOf(it) }
    fun createNames(): List<String> = wordLists().map(::wordsToName)
    fun createRelations(names: List<String>, relationsPerName: Int): List<Pair<String, String>> {
        val size = names.size
        val totalRelations = relationsPerName * size
        val forwardRelations = (1..totalRelations).map {
            random.nextInt(size) to random.nextInt(size)
        }.filter { (first, second) -> first != second }.map { (first, second) ->
            if (first > second) second to first
            else first to second
        }.distinct().map { (first, second) ->
            names[first] to names[second]
        }
        val backwardsRelations = (1..backwardsRelationsCount).map {
            random.nextInt(size) to random.nextInt(size)
        }.filter { (first, second) -> first != second }.map { (first, second) ->
            if (first < second) second to first
            else first to second
        }.distinct().map { (first, second) ->
            names[first] to names[second]
        }
        val relations = forwardRelations + backwardsRelations
        return relations
    }

    fun composeCompilationUnits(names:List<String>, relations:List<Pair<String, String>>):List<CompilationUnit>{
        val relationsByName: Map<String, List<Pair<String, String>>> = relations.groupBy{it.first}
        return names.map { name ->
            val dependencies = (relationsByName[name] ?: emptyList()).map { it.second }
            CompilationUnit(name, dependencies)
        }
    }

    private fun wordLists(): List<List<String>> {
        return buildWordLists(emptyList(), depth)
    }

    private fun buildWordLists(soFar: List<List<List<String>>>, remain: Int): List<List<String>> {
        if (remain == 0) return soFar.flatten()
        if (soFar.isEmpty()) return buildWordLists(listOf(multiplier), remain - 1)
        val last = soFar.last()
        val next = cartesianProduct(multiplier, last)
        return buildWordLists(soFar + listOf(next), remain - 1)
    }


    private fun cartesianProduct(aList: List<List<String>>, bList: List<List<String>>): List<List<String>> {
        return aList.flatMap { a ->
            bList.map { b ->
                a + b
            }
        }
    }

    private fun wordsToName(words: List<String>): String =
        words.joinToString(".")
}
