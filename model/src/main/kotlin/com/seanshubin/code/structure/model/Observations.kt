package com.seanshubin.code.structure.model

import com.seanshubin.code.structure.nameparser.NameDetail
import com.seanshubin.code.structure.relationparser.RelationDetail
import java.nio.file.Path

data class Observations(
    val inputDir: Path,
    val sourcePrefix: String,
    val sourceFiles: List<Path>,
    val sources: List<NameDetail>,
    val binaries: List<RelationDetail>,
    val missingBinaries: List<NameDetail>
)
