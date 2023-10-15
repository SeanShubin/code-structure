package com.seanshubin.code.structure.domain

data class Page(val name: String, val id: String) {
    val fileName: String get() = "$name.html"
}
