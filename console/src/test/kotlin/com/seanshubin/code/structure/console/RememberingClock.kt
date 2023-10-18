package com.seanshubin.code.structure.console

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class RememberingClock(
    private val memoryDir: Path,
    private val backup: Clock
) : Clock() {
    private val instants: List<Instant>
    private var index = 0
    private val memoryFile:Path

    init {
        Files.createDirectories(memoryDir)
        memoryFile = memoryDir.resolve("clock.txt")
        if(!Files.exists(memoryFile)){
            Files.createFile(memoryFile)
        }
        val lines = Files.readAllLines(memoryFile)
        instants = lines.map(Instant::parse)
    }

    override fun instant(): Instant =
        if (index < instants.size) {
            instants[index++]
        } else {
            val result = backup.instant()
            Files.write(memoryFile, listOf(result.toString()), StandardOpenOption.APPEND)
            result
        }

    override fun withZone(zone: ZoneId?): Clock {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getZone(): ZoneId {
        throw UnsupportedOperationException("not implemented")
    }
}