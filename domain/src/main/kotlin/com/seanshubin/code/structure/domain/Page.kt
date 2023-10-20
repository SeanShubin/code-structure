package com.seanshubin.code.structure.domain

import java.nio.file.Path

data class Page(
    val id: String,
    val name: String
) {
    fun reportFilePath(reportDir: Path):Path = reportDir.resolve(reportFileName())
    fun reportFileName():String = "$id.html"
}
