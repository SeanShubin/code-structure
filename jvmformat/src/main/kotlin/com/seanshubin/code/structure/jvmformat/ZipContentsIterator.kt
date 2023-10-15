package com.seanshubin.code.structure.jvmformat

import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ZipContentsIterator(
    private val inputStream: InputStream,
    private val name: String,
    private val isZip: (String) -> Boolean,
    private val accept: (List<String>, ZipEntry) -> Boolean
) : Iterator<ZipContents> {

    private data class History(val name: String, val zipInputStream: ZipInputStream)

    private var path: List<History> = listOf(History(name, ZipInputStream(inputStream)))
    private var maybeNextEntry: ZipEntry? = latestZipInputStream().nextEntry

    override fun hasNext(): Boolean = maybeNextEntry != null

    override fun next(): ZipContents {
        val localNextEntry = maybeNextEntry
        if (localNextEntry == null) {
            throw RuntimeException("End of iterator")
        } else {
            val bytes = loadBytes(localNextEntry)
            val result = ZipContents(pathNames(), localNextEntry, bytes)
            moveCursorForward()
            return result
        }
    }

    fun closeBackingInputStreamEarly() {
        inputStream.close()
    }

    private fun loadBytes(zipEntry: ZipEntry): List<Byte> {
        if (zipEntry.size == -1L) {
            //sometimes this api tells me the size is -1 even though there are bytes to be read
            val buffer = mutableListOf<Byte>()

            tailrec fun readRemainingBytes(): Unit {
                val theByte = latestZipInputStream().read()
                if (theByte != -1) {
                    buffer.add(theByte.toByte())
                    readRemainingBytes()
                }
            }

            readRemainingBytes()
            return buffer
        } else {
            val size = zipEntry.size.toInt()
            val bytes = mutableListOf<Byte>()
            //workaround
            //I tried doing latestZipInputStream.read(byteArray)
            //but it only populated the first 176 bytes
            //the rest were still zero
            //when I read each individual byte, it works fine
            repeat(size) {
                val byte = latestZipInputStream().read()
                bytes.add(byte.toByte())
            }
            return bytes
        }
    }

    private fun latestZipInputStream(): ZipInputStream = path[0].zipInputStream

    private fun extractName(history: History): String = history.name

    private fun pathNames(): List<String> = path.map(::extractName).reversed()

    private tailrec fun moveCursorForward() {
        if (!hasNext()) throw RuntimeException("Can't move past end of iterator")
        val entry = latestZipInputStream().nextEntry
        if (entry == null) {
            path = path.drop(1)
            if (path.isEmpty()) {
                maybeNextEntry = null
                inputStream.close()
            } else {
                moveCursorForward()
            }
        } else {
            if (entry.isDirectory) {
                maybeNextEntry = entry
            } else if (isZip(entry.name)) {
                val zipInputStream = ZipInputStream(latestZipInputStream())
                path = listOf(History(entry.name, zipInputStream)) + path
                moveCursorForward()
            } else {
                if (accept(pathNames(), entry)) {
                    maybeNextEntry = entry
                } else {
                    moveCursorForward()
                }
            }
        }
    }

    companion object ZipContentsIterator {
        val AcceptAll: (Pair<List<String>, ZipEntry>) -> Boolean = { true }
    }
}
