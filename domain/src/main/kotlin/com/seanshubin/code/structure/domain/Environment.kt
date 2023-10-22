package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.exec.Exec
import java.nio.file.Path

interface Environment {
    val files: FilesContract
    val basePath: Path
    val exec: Exec
}
