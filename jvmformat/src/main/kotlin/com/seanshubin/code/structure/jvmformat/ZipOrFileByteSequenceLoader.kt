package com.seanshubin.code.structure.jvmformat

import java.nio.file.Path

class ZipOrFileByteSequenceLoader(
    private val zipByteSequenceLoader: ZipByteSequenceLoader,
    private val fileByteSequenceLoader: FileByteSequenceLoader
) : ByteSequenceLoader {
    override fun loadByteSequences(path: Path, names: List<String>): Iterable<ByteSequence> =
        if (FileTypes.isCompressed(path.toString())) {
            zipByteSequenceLoader.loadByteSequences(path, names)
        } else {
            fileByteSequenceLoader.loadByteSequences(path, names)
        }

}
