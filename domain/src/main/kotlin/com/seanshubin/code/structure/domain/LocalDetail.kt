package com.seanshubin.code.structure.domain

data class LocalDetail(
    val name: String,
    val names: List<String>,
    val references: List<Pair<String, String>>
)
