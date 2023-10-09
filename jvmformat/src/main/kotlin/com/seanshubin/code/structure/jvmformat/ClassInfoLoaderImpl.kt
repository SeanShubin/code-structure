package com.seanshubin.code.structure.jvmformat

import java.io.ByteArrayInputStream
import java.io.DataInputStream

class ClassInfoLoaderImpl:ClassInfoLoader {
    override fun fromBytes(bytes: List<Byte>): JvmClass {
        val byteArrayInputStream = ByteArrayInputStream(bytes.toByteArray())
        return DataInputStream(byteArrayInputStream).use { dataInput ->
            JvmClass.fromDataInput(dataInput)
        }
    }
}
