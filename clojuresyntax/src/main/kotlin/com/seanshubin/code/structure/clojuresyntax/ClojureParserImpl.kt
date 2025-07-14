package com.seanshubin.code.structure.clojuresyntax

import com.seanshubin.code.structure.nameparser.NameDetail
import com.seanshubin.code.structure.nameparser.RegexUtil
import java.nio.file.Path

class ClojureParserImpl(private val relativeToDir: Path) : ClojureParser {
    private val namespaceRegex = Regex("""\(\s*ns\s+([._\-\w]+)""", RegexOption.MULTILINE)
    override fun parseName(path: Path, content: String): NameDetail {
        val relativePath = relativeToDir.relativize(path)
        val language = "java"
        val names = RegexUtil.findAllByRegex(namespaceRegex, content).map { it.replace("-", "_") }
        return NameDetail(relativePath, language, names, emptyList())
    }
}
