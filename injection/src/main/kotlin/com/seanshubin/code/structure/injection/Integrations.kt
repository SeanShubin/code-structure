package com.seanshubin.code.structure.injection

import java.time.Clock

interface Integrations {
    val clock: Clock
    val emitLine: (String) -> Unit
    val configBaseName: String
}
