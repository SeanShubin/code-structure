package com.seanshubin.code.structure.domain

object LinkCreator {
    val local: (String) -> String = { name ->
        "local-$name.html"
    }
    val none:(String)->String? = { null }
}
