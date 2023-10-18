package com.seanshubin.code.structure.console

import java.time.Clock

class ProductionIntegrations:Integrations {
    override val clock:Clock = Clock.systemUTC()
    override val emitLine: (String) -> Unit = ::println
}
