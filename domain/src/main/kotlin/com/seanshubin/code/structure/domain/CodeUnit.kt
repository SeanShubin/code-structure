package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ListUtil.startsWith

object CodeUnit {
    fun String.toGroupPath(): List<String> = split('.')
    fun List<String>.groupToCodeUnit(existing: String): String = (this + existing).joinToString(".")
    fun String.isAncestorOf(that: String): Boolean {
        val thisPath = this.toGroupPath()
        val thatPath = that.toGroupPath()
        if (thisPath.size >= thatPath.size) return false
        val thisParent = thisPath.take(thisPath.size - 1)
        return thatPath.startsWith(thisParent)
    }
}
