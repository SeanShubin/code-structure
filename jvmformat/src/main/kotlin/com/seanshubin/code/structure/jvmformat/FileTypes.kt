package com.seanshubin.code.structure.jvmformat

object FileTypes {
    private val classExt = ".class"
    private val jarExt = ".jar"
    private val warExt = ".war"
    private val relevantExtensions = listOf(classExt, jarExt, warExt)
    private val compressedExtensions = listOf(jarExt, warExt)

    fun isClass(path: String): Boolean = path.endsWith(classExt)
    fun isRelevant(path: String): Boolean = relevantExtensions.any { path.endsWith(it) }
    fun isCompressed(path: String): Boolean = compressedExtensions.any { path.endsWith(it) }
    fun isJar(path: String): Boolean = path.endsWith(jarExt)
}
