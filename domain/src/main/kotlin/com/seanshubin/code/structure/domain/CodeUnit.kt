package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ListUtil.startsWith

data class CodeUnit(val parts:List<String>) {
    fun resolve(part:String):CodeUnit = CodeUnit(parts + part)
    fun toName():String = parts.joinToString(".")
    fun toUriName(prefix:String, suffix:String):String = (listOf(prefix) + parts).joinToString("-") + suffix
    fun containing():CodeUnit = CodeUnit(parts.dropLast(1))
    fun isAncestorOf(that: CodeUnit): Boolean {
        val thisPath = this.parts
        val thatPath = that.parts
        if (thisPath.size >= thatPath.size) return false
        val thisParent = thisPath.take(thisPath.size - 1)
        return thatPath.startsWith(thisParent)
    }
    companion object {
        fun String.toCodeUnit():CodeUnit = CodeUnit(split('.'))
    }
}
