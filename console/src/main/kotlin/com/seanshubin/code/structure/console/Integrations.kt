package com.seanshubin.code.structure.console

import java.time.Clock

interface Integrations {
    val clock: Clock
    val emitLine: (String) -> Unit
}
