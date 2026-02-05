package com.seanshubin.code.structure.composition

object ArgsParser {
    fun parseConfigBaseName(args: Array<String>): String =
        args.firstOrNull()?.takeIf { it.isNotBlank() } ?: "code-structure"
}
