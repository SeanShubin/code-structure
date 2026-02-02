package com.seanshubin.code.structure.injection

import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.exec.Exec
import java.time.Clock

interface Integrations {
    val clock: Clock
    val emitLine: (String) -> Unit
    val files: FilesContract
    val exec: Exec
}
