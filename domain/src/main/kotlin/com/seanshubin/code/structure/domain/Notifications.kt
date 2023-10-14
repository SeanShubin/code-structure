package com.seanshubin.code.structure.domain

import java.time.Duration

interface Notifications {
    fun configFileEvent(configFileName:String)
    fun timeTakenEvent(timeTaken: Duration)
}
