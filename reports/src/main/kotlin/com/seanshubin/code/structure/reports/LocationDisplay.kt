package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.model.CodeUnit
import com.seanshubin.code.structure.relationparser.SourceLocation
import java.nio.file.Path

fun Path.toDisplayString(): String = toString().replace('\\', '/')

fun Path.toSourceHref(sourcePrefix: String): String = sourcePrefix + toDisplayString()

fun SourceLocation.toDisplayString(): String = when (this) {
    is SourceLocation.StandaloneFile -> file.toDisplayString()
    is SourceLocation.ZipEntry -> "${zipFile.toDisplayString()}!$entryPath"
}

fun CodeUnit.toSourceLink(sourcePrefix: String, sourceByName: Map<String, List<Path>>): String? {
    val qualifiedName = toName()
    val sources = sourceByName.getValue(qualifiedName)
    return when (sources.size) {
        1 -> sources[0].toSourceHref(sourcePrefix)
        0 -> throw RuntimeException("No source found for $qualifiedName")
        else -> null
    }
}
