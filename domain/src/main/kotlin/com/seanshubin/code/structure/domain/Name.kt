package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.collection.ListUtil.startsWith

object Name {
    fun String.toGroupPath():List<String> = split('.')
    fun List<String>.groupToName():String = joinToString(".")
    fun List<String>.groupToName(existing:String):String = (this + existing).joinToString(".")
    fun String.isAncestorOf(that:String):Boolean {
        val thisPath = this.toGroupPath()
        val thatPath = that.toGroupPath()
        if(thisPath.size >= thatPath.size) return false
        val thisParent = thisPath.take(thisPath.size-1)
        return thatPath.startsWith(thisParent)
    }

}
