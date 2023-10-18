package com.seanshubin.code.structure.domain

data class Detail(
    val name: String,
    val cycle: Set<String>?,
    val arrows: Arrows
)
