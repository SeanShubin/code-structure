package com.seanshubin.code.structure.domain

data class Page(
    val id: String,
    val name: String
) {
    val fileName: String get() = "$id.html"
}
