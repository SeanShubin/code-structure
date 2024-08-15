package com.seanshubin.code.structure.typescriptsyntax

import java.nio.file.Path

object TypeScriptRules {
    fun Path.toModuleName(): String =
        normalize().toString().toModuleName()

    fun String.toModuleName(path: Path): String =
        path.parent.resolve(this).toModuleName()

    private fun String.toModuleName():String =
        removeSrcPrefix().removeSuffix().replaceSlashesWithDots()

    private val srcPrefix = "src/"
    private fun String.removeSrcPrefix(): String = removeExistingPrefix(srcPrefix)

    private fun String.removeExistingPrefix(prefix:String): String {
        return if (startsWith(prefix)) removePrefix(prefix)
        else throw RuntimeException("Expected '$this' to start with '$prefix'")
    }

    private fun String.removeSuffix(): String =
        if (endsWith(".ts")) removeSuffix(".ts")
        else if (endsWith(".tsx")) removeSuffix(".tsx")
        else this

    private fun String.replaceSlashesWithDots(): String =
        replace("/", ".")
}
