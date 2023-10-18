package com.seanshubin.code.structure.console

import java.nio.file.Path
import java.time.Clock

class RegressionIntegrations(integrations:Integrations, memoryDir: Path):Integrations {
    override val clock: Clock = RememberingClock(memoryDir, integrations.clock)
}
