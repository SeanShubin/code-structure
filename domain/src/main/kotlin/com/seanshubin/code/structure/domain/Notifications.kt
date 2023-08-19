package com.seanshubin.code.structure.domain

import java.time.Duration

interface Notifications {
    fun timeTakenEvent(timeTaken: Duration)
}