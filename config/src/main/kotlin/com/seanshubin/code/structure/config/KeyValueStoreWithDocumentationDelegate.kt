package com.seanshubin.code.structure.config

class KeyValueStoreWithDocumentationDelegate(private val keyValueStore: KeyValueStore, private val documentationPrefix:List<String>) : KeyValueStoreWithDocumentation {
    override fun load(key: List<String>, default: Any?, documentation: List<String>): Any? {
        val documentationKey = documentationPrefix + key
        val pathLine = "path: ${key.joinToString(".")}"
        val defaultValueLine = "default value: $default"
        val defaultValueTypeName = when(default){
            null -> "<null>"
            else -> default.javaClass.simpleName
        }
        val defaultValueTypeLine = "default value type: $defaultValueTypeName"
        val commonLines = listOf(pathLine , defaultValueLine, defaultValueTypeLine)
        keyValueStore.store(documentationKey, commonLines + documentation)
        if (!keyValueStore.exists(key)) {
            keyValueStore.store(key, default)
        }
        return keyValueStore.load(key)
    }
}
