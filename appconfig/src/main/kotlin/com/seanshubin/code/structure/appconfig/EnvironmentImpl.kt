package com.seanshubin.code.structure.appconfig

import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.exec.Exec
import java.nio.file.Path

class EnvironmentImpl(
    override val files: FilesContract,
    override val basePath: Path,
    override val exec: Exec
) : Environment
