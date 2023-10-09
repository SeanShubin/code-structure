package com.seanshubin.code.structure.jvmformat

import java.nio.file.Path

interface ByteSequenceLoader {
    fun loadByteSequences(file: Path, names:List<String>):Iterable<ByteSequence>
}