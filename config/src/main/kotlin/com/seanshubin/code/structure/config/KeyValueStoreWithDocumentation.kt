package com.seanshubin.code.structure.config

interface KeyValueStoreWithDocumentation {
    fun load(key: List<String>, default: Any?, documentation: List<String>): Any?
}
