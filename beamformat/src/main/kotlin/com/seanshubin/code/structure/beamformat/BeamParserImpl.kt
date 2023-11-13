package com.seanshubin.code.structure.beamformat

import com.seanshubin.code.structure.contract.delegate.FilesContract
import com.seanshubin.code.structure.relationparser.RelationDetail
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.file.Path

class BeamParserImpl(
    private val files: FilesContract,
    private val relativeToDir: Path
) : BeamParser {
    override fun parseDependencies(path: Path, names: List<String>): List<RelationDetail> {
        val relativeDir = relativeToDir.relativize(path)
        val binaryDetail = files.newInputStream(path).use { inputStream ->
            val beamFile = parseBeamFile(inputStream)
            beamFile.toBinaryDetail(relativeDir)
        }
        return listOf(binaryDetail).mapNotNull(::elixirOnly).filter {
            names.contains(it.name)
        }.map {
            dependenciesOnly(it, names)
        }
    }

    private fun elixirOnly(relationDetail: RelationDetail): RelationDetail? {
        val elixirName = relationDetail.name.toElixirName() ?: return null
        val newDependencyNames = relationDetail.dependencyNames.mapNotNull { it.toElixirName() }
        return relationDetail.copy(
            name = elixirName,
            dependencyNames = newDependencyNames
        )
    }

    private fun dependenciesOnly(relationDetail: RelationDetail, names: List<String>): RelationDetail {
        val newDependencyNames = relationDetail.dependencyNames.filter { names.contains(it) }
        return relationDetail.copy(
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

    private fun BeamFile.toBinaryDetail(path: Path): RelationDetail {
        val name = atoms[0]
        val dependencyNames = imports.map {
            atoms[it.moduleIndex - 1]
        }.distinct().filterNot { it == name }
        val pathInFile = ""
        return RelationDetail(path, pathInFile, name, dependencyNames)
    }

    companion object {
        private const val elixirPrefix = "Elixir."
    }
}
