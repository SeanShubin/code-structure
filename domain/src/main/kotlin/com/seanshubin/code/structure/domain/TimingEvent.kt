package com.seanshubin.code.structure.domain

import java.time.Duration

data class TimingEvent(
    val category: String,
    val caption: String,
    val duration: Duration
)
