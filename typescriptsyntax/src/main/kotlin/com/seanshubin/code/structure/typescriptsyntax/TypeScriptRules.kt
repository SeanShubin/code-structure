package com.seanshubin.code.structure.typescriptsyntax

import java.nio.file.Path

object TypeScriptRules {
    fun Path.toModuleName(): String =
        normalize().toString().removeSrcPrefix().removeSuffix().replaceSlashesWithDots()

    fun String.toModuleName(path: Path): String =
        path.parent.resolve(this).normalize().toString().removeSrcPrefix().replaceSlashesWithDots()

    private val srcPrefix = "src/"
    private fun String.removeSrcPrefix(): String = removeExistingPrefix(srcPrefix)

    private fun String.removeExistingPrefix(prefix:String): String {
        return if (startsWith(prefix)) removePrefix(prefix)
        else throw RuntimeException("Expected to start with $prefix")
    }

    private fun String.removeSuffix(): String =
        if (endsWith(".ts")) removeSuffix(".ts")
        else if (endsWith(".tsx")) removeSuffix(".tsx")
        else throw RuntimeException("Expected to end with .ts or .tsx")

    private fun String.replaceSlashesWithDots(): String =
        replace("/", ".")
}
