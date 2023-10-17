package com.seanshubin.code.structure.domain

import java.time.Duration

data class TimingSummary(
    val category: String,
    val count: Int,
    val minimum: Duration,
    val maximum: Duration,
    val median: List<Duration>,
    val total: Duration
)
