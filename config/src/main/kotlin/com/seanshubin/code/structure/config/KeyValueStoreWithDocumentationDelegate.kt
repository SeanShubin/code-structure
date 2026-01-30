package com.seanshubin.code.structure.config

class KeyValueStoreWithDocumentationDelegate(
    private val keyValueStore: KeyValueStore,
    private val documentationKeyValueStore: KeyValueStore
) : KeyValueStoreWithDocumentation {
    override fun load(key: List<String>, default: Any?, documentation: List<String>): Any? {
        val pathLine = "path: ${key.joinToString(".")}"
        val defaultValueLine = "default value: $default"
        val defaultValueTypeName = when (default) {
            null -> "<null>"
            else -> default.javaClass.simpleName
        }
        val defaultValueTypeLine = "default value type: $defaultValueTypeName"
        val commonLines = listOf(pathLine, defaultValueLine, defaultValueTypeLine)
        documentationKeyValueStore.store(key, commonLines + documentation)
        if (!keyValueStore.exists(key)) {
            keyValueStore.store(key, default)
        }
        return keyValueStore.load(key)
    }
}
