package com.seanshubin.code.structure.model

import java.nio.file.Path

data class SourceLink(val displayName: String, val href: String) {
    companion object {
        fun of(sourcePrefix: String, path: Path): SourceLink {
            val displayName = path.toString().replace('\\', '/')
            return SourceLink(displayName, sourcePrefix + displayName)
        }
    }
}
