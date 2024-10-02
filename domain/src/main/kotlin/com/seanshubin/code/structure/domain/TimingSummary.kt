package com.seanshubin.code.structure.domain

import java.time.Duration

data class TimingSummary(
    val key: String,
    val count: Int,
    val minimum: Duration,
    val maximum: Duration,
    val median: List<Duration>,
    val total: Duration
)
