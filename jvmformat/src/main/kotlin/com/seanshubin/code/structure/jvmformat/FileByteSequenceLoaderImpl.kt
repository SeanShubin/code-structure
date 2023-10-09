package com.seanshubin.code.structure.jvmformat

import com.seanshubin.code.structure.contract.FilesContract
import java.nio.file.Path

class FileByteSequenceLoaderImpl(private val files: FilesContract) : FileByteSequenceLoader {
    override fun loadByteSequences(file: Path, names: List<String>): Iterable<ByteSequence> {
        val bytes = files.readAllBytes(file).toList()
        val pathInFile = ""
        val byteSequence = ByteSequence(pathInFile, bytes)
        return listOf(byteSequence)
    }
}
