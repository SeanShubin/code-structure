package com.seanshubin.code.structure.stress

object NameFormat {
    fun String.toNameParts():List<String> = split(".")
    fun List<String>.toName():String = joinToString(".")
    fun List<String>.toPackageLine(prefix:List<String>):String = "package ${toPackage(prefix)};"
    fun List<String>.toImportLine(prefix:List<String>):String = "import ${toPackage(prefix)}.${toUnqualifiedName()};"
    fun List<String>.toFieldLine():String = "private final ${toUnqualifiedName()} ${toBinding()};"
    fun List<String>.toPackageParts(prefix:List<String>):List<String> = prefix + take(size - 1).map { it.lowercase() }
    fun List<String>.toPackage(prefix:List<String>):String = toPackageParts(prefix).joinToString(".")
    fun List<String>.toUnqualifiedName():String = joinToString("")
    fun List<String>.toBinding():String = toUnqualifiedName()[0].lowercase() + toUnqualifiedName().drop(1)
    fun List<String>.toUnqualifiedFileName():String = toUnqualifiedName() + ".java"
    fun List<String>.toFileName(prefix:List<String>):String = (toPackageParts(prefix) + toUnqualifiedFileName()).joinToString("/")
}
