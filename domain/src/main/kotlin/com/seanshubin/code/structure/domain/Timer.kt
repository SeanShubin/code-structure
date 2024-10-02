package com.seanshubin.code.structure.domain

interface Timer {
    fun <T> monitor(source:String, category: String, f: () -> T): T
    fun <T> monitor(source:String, category: String, caption: String, f: () -> T): T
    fun events(): List<TimingEvent>
    fun summaries(): List<TimingSummary>
}
