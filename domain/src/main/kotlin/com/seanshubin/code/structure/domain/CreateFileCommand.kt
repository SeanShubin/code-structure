package com.seanshubin.code.structure.domain

import java.nio.file.Path

data class CreateFileCommand(val path: Path, val lines: List<String>) : Command
