package com.seanshubin.code.structure.domain

import java.nio.file.Path
import java.time.Duration

interface Notifications {
    fun configFileEvent(configFile: Path)
    fun timeTakenEvent(timeTaken: Duration)
    fun errorEvent(errorDetail: ErrorDetail)
}
