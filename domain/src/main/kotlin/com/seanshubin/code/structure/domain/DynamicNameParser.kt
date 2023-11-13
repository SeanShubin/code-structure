package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.elixirsyntax.ElixirParser
import com.seanshubin.code.structure.javasyntax.JavaParser
import com.seanshubin.code.structure.kotlinsyntax.KotlinParser
import com.seanshubin.code.structure.nameparser.NameDetail
import com.seanshubin.code.structure.nameparser.NameParser
import com.seanshubin.code.structure.scalasyntax.ScalaParser
import java.nio.file.Path
import kotlin.io.path.extension

class DynamicNameParser(
    kotlinParser: KotlinParser,
    elixirParser: ElixirParser,
    scalaParser: ScalaParser,
    javaParser: JavaParser
) : NameParser {
    private val parserByExtension = mapOf(
        "scala" to scalaParser,
        "kt" to kotlinParser,
        "java" to javaParser,
        "ex" to elixirParser
    )

    override fun parseName(path: Path, content: String): NameDetail {
        val extension = path.extension
        val parser =
            parserByExtension[extension] ?: throw RuntimeException("No parser defined for extension '$extension'")
        return parser.parseName(path, content)
    }
}
