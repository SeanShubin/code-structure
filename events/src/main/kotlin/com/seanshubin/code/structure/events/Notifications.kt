package com.seanshubin.code.structure.events

import com.seanshubin.code.structure.model.Summary
import java.nio.file.Path
import java.time.Duration

interface Notifications {
    fun configFileEvent(configFile: Path)
    fun timeTakenEvent(caption: String, timeTaken: Duration)
    fun fullAppTimeTakenEvent(timeTaken: Duration)
    fun summaryEvent(summary: Summary)
}
