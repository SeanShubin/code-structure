package com.seanshubin.code.structure.config

import com.fasterxml.jackson.module.kotlin.readValue
import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.json.JsonMappers
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class JsonFileKeyValueStore(val path: Path, val files: FilesContract) : KeyValueStore {
    override fun load(key: List<String>): Any? {
        val jsonObject = loadJsonObject()
        return DynamicUtil.get(jsonObject, key)
    }

    override fun store(key: List<String>, value: Any?) {
        val jsonObject = loadJsonObject()
        val newJsonObject = DynamicUtil.set(jsonObject, key, value)
        val newJsonText = JsonMappers.pretty.writeValueAsString(newJsonObject)
        files.writeString(path, newJsonText, jsonCharset)
    }

    override fun exists(key: List<String>): Boolean {
        if (!files.exists(path)) return false
        val jsonObject = loadJsonObject()
        return DynamicUtil.exists(jsonObject, key)
    }

    private fun loadJsonObject(): Any? {
        val text = if (files.exists(path)) {
            files.readString(path, jsonCharset)
        } else {
            DEFAULT_JSON_TEXT
        }
        val jsonText = text.ifBlank { DEFAULT_JSON_TEXT }
        val jsonObject = JsonMappers.parser.readValue<Any?>(jsonText)
        return jsonObject
    }

    companion object {
        private val jsonCharset = StandardCharsets.UTF_8
        private const val DEFAULT_JSON_TEXT = "{}"
    }
}
