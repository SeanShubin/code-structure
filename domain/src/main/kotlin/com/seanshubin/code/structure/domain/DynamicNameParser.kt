package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.clojuresyntax.ClojureParser
import com.seanshubin.code.structure.elixirsyntax.ElixirParser
import com.seanshubin.code.structure.javasyntax.JavaParser
import com.seanshubin.code.structure.kotlinsyntax.KotlinParser
import com.seanshubin.code.structure.nameparser.NameDetail
import com.seanshubin.code.structure.nameparser.NameParser
import com.seanshubin.code.structure.scalasyntax.ScalaParser
import com.seanshubin.code.structure.typescriptsyntax.TypeScriptNameParser
import java.nio.file.Path
import kotlin.io.path.extension

class DynamicNameParser(
    kotlinParser: KotlinParser,
    elixirParser: ElixirParser,
    scalaParser: ScalaParser,
    javaParser: JavaParser,
    typeScriptParser: TypeScriptNameParser,
    clojureParser: ClojureParser
) : NameParser {
    private val parserByExtension = mapOf(
        "scala" to scalaParser,
        "kt" to kotlinParser,
        "java" to javaParser,
        "ex" to elixirParser,
        "tsx" to typeScriptParser,
        "ts" to typeScriptParser,
        "clj" to clojureParser
    )

    override fun parseName(path: Path, content: String): NameDetail {
        val extension = path.extension
        val parser =
            parserByExtension[extension]
                ?: throw RuntimeException("No name parser defined for extension '$extension' on file '$path'")
        return parser.parseName(path, content)
    }
}
