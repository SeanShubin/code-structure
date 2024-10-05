package com.seanshubin.code.structure.domain

import java.time.Duration

data class TimingEvent(
    val source: String,
    val category: String,
    val caption: String,
    val duration: Duration
) {
    val key: String get() = "$source-$category"
}
