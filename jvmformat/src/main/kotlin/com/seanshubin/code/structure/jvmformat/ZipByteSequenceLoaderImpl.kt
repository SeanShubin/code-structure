package com.seanshubin.code.structure.jvmformat

import com.seanshubin.code.structure.contract.delegate.FilesContract
import java.nio.file.Path
import java.util.zip.ZipEntry

class ZipByteSequenceLoaderImpl(
    private val files: FilesContract
) : ZipByteSequenceLoader {
    override fun loadByteSequences(file: Path, names: List<String>): Iterable<ByteSequence> {
        fun acceptEntry(path: List<String>, entry: ZipEntry): Boolean {
            val className = zipEntryNameToClassName(entry.name)
            return names.contains(className)
        }
        return files.newInputStream(file).use { inputStream ->
            val iterator = ZipContentsIterator(inputStream, file.toString(), ::isCompressed, ::acceptEntry)
            val bytesIterable = iterator.asSequence().filter(::zipContentsRelevant).map { zipContents ->
                ByteSequence(zipContents.zipEntry.name, zipContents.bytes)
            }.toList()
            reifyBeforeStreamCloses(bytesIterable)
        }
    }

    private fun zipContentsRelevant(zipContents: ZipContents): Boolean =
        FileTypes.isClass(zipContents.zipEntry.name)

    private fun reifyBeforeStreamCloses(x: Iterable<ByteSequence>): Iterable<ByteSequence> =
        x.toList()

    private fun isCompressed(name: String): Boolean = FileTypes.isCompressed(name)

    private fun zipEntryNameToClassName(zipEntryName: String): String? {
        if (!zipEntryName.endsWith(classSuffix)) return null
        val withoutClassSuffix = zipEntryName.substring(0, zipEntryName.length - classSuffix.length)
        val longClassName = withoutClassSuffix.replace('/', '.')
        val dollarIndex = longClassName.indexOf('$')
        val className = if (dollarIndex == -1) {
            longClassName
        } else {
            longClassName.substring(0, dollarIndex)
        }
        return className
    }

    companion object {
        private val classSuffix = ".class"
    }
}
