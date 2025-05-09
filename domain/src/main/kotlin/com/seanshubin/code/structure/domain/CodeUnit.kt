package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ListUtil.startsWith
import java.nio.file.Path

data class CodeUnit(val parts: List<String>) {
    fun parent(): CodeUnit = CodeUnit(parts.dropLast(1))
    fun resolve(part: String): CodeUnit = CodeUnit(parts + part)
    fun toName(): String = parts.joinToString(".")
    fun toUriName(prefix: String, suffix: String): String = id(prefix) + suffix
    fun toSourceLink(sourcePrefix:String, sourceByName:Map<String, List<Path>>):String? {
        val qualifiedName = toName()
        val sources = sourceByName.getValue(qualifiedName)
        return when(sources.size){
            1 -> sourcePrefix + sources[0].toString()
            0 -> throw RuntimeException("No source found for $qualifiedName")
            else -> null
        }
    }
    fun caption(prefix: String): String = (listOf(prefix) + toNameAsList()).joinToString(" ")
    fun id(prefix: String): String = (listOf(prefix) + parts).joinToString("-")
    fun isAncestorOf(that: CodeUnit): Boolean {
        val thisPath = this.parts
        val thatPath = that.parts
        if (thisPath.size >= thatPath.size) return false
        val thisParent = thisPath.take(thisPath.size - 1)
        return thatPath.startsWith(thisParent)
    }

    private fun toNameAsList(): List<String> = if (parts.isEmpty()) emptyList() else listOf(toName())

    companion object {
        fun String.toCodeUnit(): CodeUnit = CodeUnit(split('.'))
    }
}
