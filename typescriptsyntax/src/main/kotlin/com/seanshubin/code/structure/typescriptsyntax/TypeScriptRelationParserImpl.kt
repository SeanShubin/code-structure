package com.seanshubin.code.structure.typescriptsyntax

import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.relationparser.RelationDetail
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptRules.toModuleName
import java.nio.charset.Charset
import java.nio.file.Path

class TypeScriptRelationParserImpl(
    private val files: FilesContract,
    private val charset: Charset
) : TypeScriptRelationParser {
    override fun parseDependencies(path: Path, names: List<String>): List<RelationDetail> {
        val text = getText(path)
        val allDependencies = searchAllDependencies(path, text)
        val dependencies = allDependencies.filter { dependency ->
            names.contains(dependency)
        }
        val pathInFile = ""
        val name = path.toModuleName()
        return listOf(RelationDetail(path, pathInFile, name, dependencies))
    }

    private fun getText(path: Path): String {
        return files.readString(path, charset)
    }

    private fun searchAllDependencies(path: Path, text: String): List<String> {
        val matches = dependsOnRegexList.flatMap { regex ->
            regex.findAll(text).map { matchResult ->
                matchResult.groupValues[1].toModuleName(path)
            }
        }.sorted().distinct()
        return matches
    }

    companion object {
        private val dependsOnRegexSQuote = Regex("from\\s+'(.*)'")
        private val dependsOnRegexDQuote = Regex("from\\s+\"(.*)\"")
        private val dependsOnRegexList = listOf(dependsOnRegexSQuote, dependsOnRegexDQuote)
    }
}
