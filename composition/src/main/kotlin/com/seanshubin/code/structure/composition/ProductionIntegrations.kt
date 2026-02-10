package com.seanshubin.code.structure.composition

import com.seanshubin.code.structure.contract.delegate.FilesDelegate
import com.seanshubin.code.structure.exec.ExecImpl
import java.time.Clock

class ProductionIntegrations(
    override val commandLineArgs: Array<String>
) : Integrations {
    override val clock = Clock.systemUTC()
    override val emitLine: (String) -> Unit = { println(it) }
    override val files = FilesDelegate
    override val exec = ExecImpl()
}
