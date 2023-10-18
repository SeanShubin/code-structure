package com.seanshubin.code.structure.domain

object Name {
    fun String.toGroupPath():List<String> = split('.')
    fun List<String>.groupToName():String = joinToString(".")
    fun List<String>.groupToName(existing:String):String = (this + existing).joinToString(".")
}
