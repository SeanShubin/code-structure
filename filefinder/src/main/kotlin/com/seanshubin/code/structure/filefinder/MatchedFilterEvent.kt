package com.seanshubin.code.structure.filefinder

data class MatchedFilterEvent(
    val category: String,
    val type: String,
    val pattern: String,
    val file: String
)
