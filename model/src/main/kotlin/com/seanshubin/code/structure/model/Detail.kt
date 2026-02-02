package com.seanshubin.code.structure.model

data class Detail(
    val name: String,
    val cycle: Set<String>?,
    val arrows: Arrows
)
