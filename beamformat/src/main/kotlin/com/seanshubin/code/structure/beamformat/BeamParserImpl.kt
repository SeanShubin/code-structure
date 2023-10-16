package com.seanshubin.code.structure.beamformat

import com.seanshubin.code.structure.binaryparser.BinaryDetail
import com.seanshubin.code.structure.contract.FilesContract
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.file.Path

class BeamParserImpl(
    private val files: FilesContract,
    private val relativeToDir: Path
) : BeamParser {
    override fun parseBinary(path: Path, names: List<String>): List<BinaryDetail> {
        val relativeDir = relativeToDir.relativize(path)
        val binaryDetail = files.newInputStream(path).use { inputStream ->
            val beamFile = parseBeamFile(inputStream)
            beamFile.toBinaryDetail(relativeDir)
        }
        return listOf(binaryDetail).mapNotNull(::elixirOnly).map{dependenciesOnly(it, names)}
    }

    private fun elixirOnly(binaryDetail: BinaryDetail): BinaryDetail? {
        val elixirName = binaryDetail.name.toElixirName() ?: return null
        val newDependencyNames = binaryDetail.dependencyNames.mapNotNull { it.toElixirName() }
        return binaryDetail.copy(
            name = elixirName,
            dependencyNames = newDependencyNames
        )
    }

    private fun dependenciesOnly(binaryDetail: BinaryDetail, names:List<String>): BinaryDetail {
        val newDependencyNames = binaryDetail.dependencyNames.filter { names.contains(it) }
        return binaryDetail.copy(
            dependencyNames = newDependencyNames
        )
    }

    private fun String.toElixirName(): String? =
        if (startsWith(elixirPrefix)) {
            substring(elixirPrefix.length)
        } else {
            null
        }

    private fun parseBeamFile(inputStream: InputStream): BeamFile {
        val beamInputStream = BeamInputStream(inputStream)
        beamInputStream.consumeStringLiteral("FOR1")
        val fileSize = beamInputStream.consumeInt()
        beamInputStream.consumeStringLiteral("BEAM")
        val sections = beamInputStream.consumeSections()
        val sectionByName = sections.associateBy { it.name }
        val atomsSection = sectionByName.getValue("AtU8")
        val atomInputStream = BeamInputStream(ByteArrayInputStream(atomsSection.bytes.toByteArray()))
        val atoms = atomInputStream.consumeAtoms()
        val importsSection = sectionByName.getValue("ImpT")
        val importsInputStream = BeamInputStream(ByteArrayInputStream(importsSection.bytes.toByteArray()))
        val imports = importsInputStream.consumeImports()
        return BeamFile(fileSize, atoms, imports, sections)
    }

    private fun BeamFile.toBinaryDetail(path: Path): BinaryDetail {
        val name = atoms[0]
        val dependencyNames = imports.map {
            atoms[it.moduleIndex - 1]
        }.distinct().filterNot { it == name }
        val pathInFile = ""
        return BinaryDetail(path, pathInFile, name, dependencyNames)
    }

    companion object {
        private const val elixirPrefix = "Elixir."
    }
}
