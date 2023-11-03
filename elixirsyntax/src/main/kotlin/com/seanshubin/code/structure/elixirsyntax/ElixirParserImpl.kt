package com.seanshubin.code.structure.elixirsyntax

import com.seanshubin.code.structure.nameparser.RegexUtil.findRegex
import com.seanshubin.code.structure.nameparser.NameDetail
import java.nio.file.Path

class ElixirParserImpl(private val relativeToDir: Path) : ElixirParser {
    private val moduleRegex = Regex("""^(?:[^\n ].*)?defmodule +([.\w]*)""", RegexOption.MULTILINE)
    override fun parseName(path: Path, content: String): NameDetail {
        val relativePath = relativeToDir.relativize(path)
        val language = "elixir"
        val modules = findRegex(moduleRegex, content)
        val errorLines = mutableListOf<String>()
        if (modules.size > 1) {
            errorLines.add("too many modules in $relativePath, found ${modules.size}")
            modules.forEachIndexed { index, line ->
                errorLines.add("  [$index] '$line'")
            }
            return NameDetail(
                relativePath,
                language,
                emptyList(),
                errorLines
            )
        }
        return NameDetail(
            relativePath,
            language,
            modules,
            errorLines
        )
    }
}
