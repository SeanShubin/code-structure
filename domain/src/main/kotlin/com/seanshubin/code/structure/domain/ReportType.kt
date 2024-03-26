package com.seanshubin.code.structure.domain

enum class ReportType(val shouldDisplayDirectCycles:Boolean) {
    FAST(shouldDisplayDirectCycles = false),
    DETAILED(shouldDisplayDirectCycles = true)
}
