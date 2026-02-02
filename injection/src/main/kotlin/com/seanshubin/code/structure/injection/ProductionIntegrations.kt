package com.seanshubin.code.structure.injection

import com.seanshubin.code.structure.contract.delegate.FilesDelegate
import com.seanshubin.code.structure.exec.ExecImpl
import java.time.Clock

object ProductionIntegrations : Integrations {
    override val clock = Clock.systemUTC()
    override val emitLine: (String) -> Unit = { println(it) }
    override val files = FilesDelegate
    override val exec = ExecImpl()
}
