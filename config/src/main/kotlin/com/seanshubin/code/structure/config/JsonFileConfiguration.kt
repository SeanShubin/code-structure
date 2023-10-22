package com.seanshubin.code.structure.config

import com.fasterxml.jackson.module.kotlin.readValue
import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.json.JsonMappers
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class JsonFileConfiguration(
    private val files: FilesContract,
    private val file: Path
) : Configuration {
    override fun load(path: List<String>, default: Any?): Any? {
        ensureFileExists()
        if (!exists(path)) {
            storeString(path, default)
        }
        val theObject = loadObject()
        val value = if (DynamicUtil.pathExists(path, theObject)) {
            DynamicUtil.getValueAtPath(path, theObject)
        } else {
            default
        }
        return value
    }

    private fun storeString(path: List<String>, value: Any?) {
        ensureFileExists()
        val theObject = loadObject()
        val newObject = DynamicUtil.setValueAtPath(path, theObject, value)
        storeObject(newObject)
    }

    private fun exists(path: List<String>): Boolean {
        if (!files.exists(file)) return false
        val theObject = loadObject()
        return DynamicUtil.pathExists(path, theObject)
    }

    private fun loadObject(): Any? {
        val text = files.readString(file, StandardCharsets.UTF_8)
        val value = JsonMappers.parser.readValue<Any?>(text)
        return value
    }

    private fun storeObject(theObject: Any?) {
        val text = JsonMappers.pretty.writeValueAsString(theObject)
        files.writeString(file, text, StandardCharsets.UTF_8)
    }

    private fun ensureFileExists() {
        if (!files.exists(file)) {
            val parent = file.parent
            if (parent != null) {
                files.createDirectories(parent)
            }
            files.writeString(file, "{}", StandardCharsets.UTF_8)
        }
    }
}
