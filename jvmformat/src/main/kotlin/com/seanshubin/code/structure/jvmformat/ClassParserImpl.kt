package com.seanshubin.code.structure.jvmformat

import com.seanshubin.code.structure.relationparser.RelationDetail
import java.nio.file.Path

class ClassParserImpl(
    private val relativeToDir: Path,
    private val byteSequenceLoader: ByteSequenceLoader,
    private val classInfoLoader: ClassInfoLoader,
    private val includeJvmDynamicInvocations: Boolean
) : ClassParser {
    override fun parseDependencies(path: Path, rawNames: List<String>): List<RelationDetail> {
        val names = rawNames.map { it.formatClassName() }
        val relativeDir = relativeToDir.relativize(path)
        val byteSequences = byteSequenceLoader.loadByteSequences(path, names)
        val binaryDetailList = byteSequences.map { byteSequence ->
            val jvmClass = classInfoLoader.fromBytes(byteSequence.bytes)
            val binaryDetail = toBinaryDetail(relativeDir, byteSequence.pathInFile, jvmClass, names)
            binaryDetail
        }
        return binaryDetailList
    }

    private fun toBinaryDetail(
        file: Path,
        pathInFile: String,
        jvmClass: JvmClass,
        names: List<String>
    ): RelationDetail {
        val name = jvmClass.thisClassName.formatClassName()
        val allDependencyNames = if (includeJvmDynamicInvocations) {
            jvmClass.constantPool.filterIsInstance<ConstantPoolInfo.Companion.Utf8Info>().map { it.value }
                .map { it.formatClassName() }
        } else {
            jvmClass.dependencyNames.map { it.formatClassName() }
        }
        val filteredDependencyNames = allDependencyNames.filter {
            names.contains(it)
        }.filterNot {
            it == name
        }
        return RelationDetail(file, pathInFile, name, filteredDependencyNames)
    }

    private fun String.formatClassName(): String {
        val dollarIndex = this.indexOf('$')
        val dollarRemoved = if (dollarIndex == -1) this else {
            this.substring(0, dollarIndex)
        }
        return dollarRemoved.replace('/', '.').replace('-', '_')
    }
}
