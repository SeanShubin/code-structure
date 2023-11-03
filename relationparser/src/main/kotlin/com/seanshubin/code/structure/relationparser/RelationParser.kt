package com.seanshubin.code.structure.relationparser

import java.nio.file.Path

interface RelationParser {
    fun parseDependencies(path: Path, names: List<String>): List<RelationDetail>
}
