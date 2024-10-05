package com.seanshubin.code.structure.typescriptsyntax

import com.seanshubin.code.structure.nameparser.NameDetail
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptRules.toModuleName
import java.nio.file.Path

class TypeScriptNameParserImpl : TypeScriptNameParser {
    override fun parseName(path: Path, content: String): NameDetail {
        val language = "typescript"
        val modules = listOf(path.toModuleName())
        val errorLines = emptyList<String>()
        return NameDetail(path, language, modules, errorLines)
    }
}
