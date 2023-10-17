package com.seanshubin.code.structure.domain

interface Timer {
    fun <T> monitor(category: String, f: () -> T): T
    fun <T> monitor(category: String, caption: String, f: () -> T): T
    fun events(): List<TimingEvent>
    fun summaries(): List<TimingSummary>
}
