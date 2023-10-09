package com.seanshubin.code.structure.beamformat

import java.io.InputStream
import java.nio.ByteBuffer

class BeamInputStream(private val inputStream: InputStream) {
    fun consumeStringLiteral(value: String) {
        var index = 0
        while (index < value.length) {
            val actualInt = inputStream.read()
            val expectedChar = value[index]
            if (actualInt == -1) {
                throw RuntimeException("Expected '$expectedChar', got end of file")
            }
            val actualChar = actualInt.toChar()
            if (expectedChar != actualChar) {
                throw RuntimeException("Expected '$expectedChar', got '$actualChar'")
            }
            index++
        }
    }

    fun consumeInt(): Int {
        var index = 0
        val byteList = mutableListOf<Byte>()
        while (index < 4) {
            val byteAsInt = inputStream.read()
            if (byteAsInt == -1) {
                throw RuntimeException("Expecting 4 bytes, got end of file")
            }
            byteList.add(byteAsInt.toByte())
            index++
        }
        return byteList.toInt()
    }

    fun consumeSections(): List<Section> {
        val sections = mutableListOf<Section>()
        var section = consumeSectionOrNull()
        while (section != null) {
            sections.add(section)
            section = consumeSectionOrNull()
        }
        return sections
    }

    fun consumeAtoms(): List<String> {
        val atomCount = consumeInt()
        val atoms = mutableListOf<String>()
        (0 until atomCount).forEach {
            val atom = consumeAtom()
            atoms.add(atom)
        }
        return atoms
    }

    fun consumeImports(): List<Import> {
        val importCount = consumeInt()
        val imports = mutableListOf<Import>()
        (0 until importCount).forEach {
            val moduleIndex = consumeInt()
            val functionIndex = consumeInt()
            val arity = consumeInt()
            val import = Import(moduleIndex, functionIndex, arity)
            imports.add(import)
        }
        return imports
    }

    private fun consumeSectionOrNull(): Section? {
        val name = consumeStringOfSizeOrNull(4) ?: return null
        val size = consumeInt()
        val bytes = consumeBytes(size)
        return Section(name, size, bytes)
    }

    private fun consumeStringOfSizeOrNull(size: Int): String? {
        var index = 0
        val chars = mutableListOf<Char>()
        while (index < size) {
            val actualInt = inputStream.read()
            if (actualInt == -1) {
                return null
            }
            val actualChar = actualInt.toChar()
            chars.add(actualChar)
            index++
        }
        return chars.joinToString("")
    }

    private fun consumeBytes(size: Int): List<Byte> {
        var index = 0
        val byteList = mutableListOf<Byte>()
        while (index < size) {
            val byteAsInt = inputStream.read()
            if (byteAsInt == -1) {
                throw RuntimeException("Expecting $size bytes, got end of file after $index")
            }
            byteList.add(byteAsInt.toByte())
            index++
        }
        while (index % 4 != 0) {
            inputStream.read()
            index++
        }
        return byteList
    }

    private fun consumeAtom(): String {
        val size = inputStream.read()
        if (size == -1) {
            throw RuntimeException("Expected atom size, got end of file")
        }
        return consumeStringOfSize(size)
    }

    private fun consumeStringOfSize(size: Int): String {
        var index = 0
        val chars = mutableListOf<Char>()
        while (index < size) {
            val actualInt = inputStream.read()
            if (actualInt == -1) {
                throw RuntimeException("Expected string of size $size, got end of file")
            }
            val actualChar = actualInt.toChar()
            chars.add(actualChar)
            index++
        }
        return chars.joinToString("")
    }

    private fun List<Byte>.toInt(): Int = ByteBuffer.wrap(this.toByteArray()).int
}