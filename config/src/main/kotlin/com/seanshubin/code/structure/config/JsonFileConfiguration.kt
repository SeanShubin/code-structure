package com.seanshubin.code.structure.config

import com.seanshubin.code.structure.untyped.Untyped
import java.nio.file.Path

class JsonFileConfiguration(private val path: Path):Configuration {
    override fun load(default: Any, path: List<String>): Untyped {
        throw UnsupportedOperationException("not implemented")
    }
}