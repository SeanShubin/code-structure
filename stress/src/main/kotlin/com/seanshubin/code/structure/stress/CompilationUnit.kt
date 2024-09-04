package com.seanshubin.code.structure.stress

import com.seanshubin.code.structure.stress.NameFormat.toBinding
import com.seanshubin.code.structure.stress.NameFormat.toFieldLine
import com.seanshubin.code.structure.stress.NameFormat.toImportLine
import com.seanshubin.code.structure.stress.NameFormat.toNameParts
import com.seanshubin.code.structure.stress.NameFormat.toPackageLine
import com.seanshubin.code.structure.stress.NameFormat.toPackageParts
import com.seanshubin.code.structure.stress.NameFormat.toUnqualifiedFileName
import com.seanshubin.code.structure.stress.NameFormat.toUnqualifiedName

class CompilationUnit(
    val name: String,
    val dependencies: List<String>
) {
    fun relativeFileName(prefix: List<String>): String {
        val parts = name.toNameParts()
        val packageParts = parts.toPackageParts(prefix)
        val unqualifiedFileName = parts.toUnqualifiedFileName()
        val mavenParts = listOf("src", "main", "java")
        val allParts = mavenParts + packageParts + unqualifiedFileName
        return allParts.joinToString("/")
    }

    fun lines(prefix: List<String>): List<String> {
        val lines = mutableListOf<String>()
        val parts = name.toNameParts()
        lines.add(parts.toPackageLine(prefix))
        lines.add("")
        val importLines = dependencies.map { it.toNameParts().toImportLine(prefix) }
        lines.addAll(importLines)
        lines.add("")
        lines.add("public class ${parts.toUnqualifiedName()} implements Runnable {")
        lines.addAll(dependencies.map { "    " + it.toNameParts().toFieldLine() })
        lines.add("")
        lines.add("    public ${parts.toUnqualifiedName()}(")
        lines.addAll(dependencies.map {
            "        ${it.toNameParts().toUnqualifiedName()} ${
                it.toNameParts().toBinding()
            }"
        }.mapExceptLast { "$it," })
        lines.add("    ) {")
        lines.addAll(dependencies.map {
            "        this.${it.toNameParts().toBinding()} = ${
                it.toNameParts().toBinding()
            };"
        })
        lines.add("    }")
        lines.add("")
        lines.add("    @Override")
        lines.add("    public void run() {")
        lines.add("        System.out.println(\"${parts.toUnqualifiedName()}\");")
        lines.addAll(dependencies.map { "        ${it.toNameParts().toBinding()}.run();" })
        lines.add("    }")
        lines.add("}")
        return lines
    }

    private fun List<String>.mapExceptLast(f: (String) -> String): List<String> =
        if (size < 2) this
        else take(size - 1).map(f) + last()
}
