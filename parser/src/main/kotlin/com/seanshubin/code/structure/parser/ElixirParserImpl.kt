package com.seanshubin.code.structure.parser

import com.seanshubin.code.structure.parser.RegexUtil.findRegex
import java.nio.file.Path

class ElixirParserImpl : ElixirParser {
    private val moduleRegex = Regex("""^(?:[^\n ].*)?defmodule +([.\w]*)""", RegexOption.MULTILINE)
    override fun parseSource(path: Path, content: String): SourceDetail {
        val language = "elixir"
        val modules = findRegex(moduleRegex, content)
        val errorLines = mutableListOf<String>()
        if (modules.size > 1) {
            errorLines.add("too many modules in $path, found ${modules.size}")
            modules.forEachIndexed { index, line ->
                errorLines.add("  [$index] '$line'")
            }
            return SourceDetail(
                path,
                language,
                emptyList(),
                errorLines
            )
        }
        return SourceDetail(
            path,
            language,
            modules,
            errorLines
        )
    }
}
