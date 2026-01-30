package com.seanshubin.code.structure.config

interface KeyValueStore {
    fun load(key: List<String>): Any?
    fun store(key: List<String>, value: Any?)
    fun exists(key: List<String>): Boolean
}
