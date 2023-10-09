package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.bytecodeformat.BinaryDetail
import com.seanshubin.code.structure.parser.SourceDetail
import java.nio.file.Path

data class Observations(
    val inputDir: Path,
    val sourcePrefix: String,
    val sourceFiles: List<Path>,
    val sources: List<SourceDetail>,
    val binaries: List<BinaryDetail>
)
