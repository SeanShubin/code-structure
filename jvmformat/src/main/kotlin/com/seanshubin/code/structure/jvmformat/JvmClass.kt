package com.seanshubin.code.structure.jvmformat


import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.ClassInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.DoubleInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.FieldRefInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.FloatInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.IntegerInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.InterfaceMethodRefInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.InvokeDynamicInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.LongInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.MethodHandleInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.MethodRefInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.MethodTypeInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.ModuleInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.NameAndTypeInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.PackageInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.StringInfo
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.Unusable
import com.seanshubin.code.structure.jvmformat.ConstantPoolInfo.Companion.Utf8Info
import java.io.DataInput

data class JvmClass(
    val magic: Int,
    val minorVersion: Short,
    val majorVersion: Short,
    val constantPoolCountPlusOne: Short,
    val constantPool: List<ConstantPoolInfo>,
    val accessFlags: Short,
    val thisClass: Short
) {
    val thisClassName: String
        get() {
            val classInfo = constantPool[thisClass.toInt()] as ClassInfo
            val utf8Info = constantPool[classInfo.nameIndex.toInt()] as Utf8Info
            return utf8Info.value
        }

    fun ClassInfo.toName(): String {
        val utf8Info = constantPool[nameIndex.toInt()] as Utf8Info
        return utf8Info.value
    }

    val dependencyNames: List<String>
        get() {
            val classNames = constantPool.filterIsInstance<ClassInfo>().map {
                it.toName()
            }
            val nameMatchesThis: (String) -> Boolean = { name -> name == thisClassName }
            val classNamesBesidesThis = classNames.filterNot(nameMatchesThis)
            return classNamesBesidesThis
        }

    companion object {
        const val TagClass: Byte = 7
        const val TagFieldref: Byte = 9
        const val TagMethodref: Byte = 10
        const val TagInterfaceMethodref: Byte = 11
        const val TagString: Byte = 8
        const val TagInteger: Byte = 3
        const val TagFloat: Byte = 4
        const val TagLong: Byte = 5
        const val TagDouble: Byte = 6
        const val TagNameAndType: Byte = 12
        const val TagUtf8: Byte = 1
        const val TagMethodHandle: Byte = 15
        const val TagMethodType: Byte = 16
        const val TagInvokeDynamic: Byte = 18
        const val TagModule: Byte = 19
        const val TagPackage: Byte = 20

        fun fromDataInput(input: DataInput): JvmClass {
            val magic = input.readInt()
            val minorVersion = input.readShort()
            val majorVersion = input.readShort()
            val constantPoolCount = input.readShort()

            tailrec fun readRemainingConstants(
                soFar: List<ConstantPoolInfo>,
                remainingIndices: Int
            ): List<ConstantPoolInfo> =
                if (remainingIndices == 0) soFar
                else {
                    val constantPoolInfo = readConstant(input)
                    if (takesTwoSlots(constantPoolInfo)) {
                        readRemainingConstants(soFar + constantPoolInfo + Unusable, remainingIndices - 2)
                    } else {
                        readRemainingConstants(soFar + constantPoolInfo, remainingIndices - 1)
                    }
                }

            val constantPool = readRemainingConstants(listOf(Unusable), constantPoolCount - 1)
            val accessFlags = input.readShort()
            val thisClass = input.readShort()
            val classFileInfo = JvmClass(
                magic,
                minorVersion,
                majorVersion,
                constantPoolCount,
                constantPool,
                accessFlags,
                thisClass
            )
            return classFileInfo
        }

        fun takesTwoSlots(constantPoolInfo: ConstantPoolInfo): Boolean = when (constantPoolInfo) {
            is DoubleInfo -> true
            is LongInfo -> true
            else -> false
        }

        fun readConstant(input: DataInput): ConstantPoolInfo {
            val tag: Byte = input.readByte()
            val info: ConstantPoolInfo = when (tag) {
                TagClass -> {
                    val nameIndex = input.readShort()
                    ClassInfo(nameIndex)
                }

                TagFieldref -> {
                    val classIndex = input.readShort()
                    val nameAndTypeIndex = input.readShort()
                    FieldRefInfo(classIndex, nameAndTypeIndex)
                }

                TagMethodref -> {
                    val classIndex = input.readShort()
                    val nameAndTypeIndex = input.readShort()
                    MethodRefInfo(classIndex, nameAndTypeIndex)
                }

                TagInterfaceMethodref -> {
                    val classIndex = input.readShort()
                    val nameAndTypeIndex = input.readShort()
                    InterfaceMethodRefInfo(classIndex, nameAndTypeIndex)
                }

                TagString -> {
                    val stringIndex = input.readShort()
                    StringInfo(stringIndex)
                }

                TagInteger -> {
                    val value = input.readInt()
                    IntegerInfo(value)
                }

                TagFloat -> {
                    val value = input.readFloat()
                    FloatInfo(value)
                }

                TagLong -> {
                    val value = input.readLong()
                    LongInfo(value)
                }

                TagDouble -> {
                    val value = input.readDouble()
                    DoubleInfo(value)
                }

                TagNameAndType -> {
                    val nameIndex = input.readShort()
                    val descriptorIndex = input.readShort()
                    NameAndTypeInfo(nameIndex, descriptorIndex)
                }

                TagUtf8 -> {
                    val value = input.readUTF()
                    Utf8Info(value)
                }

                TagMethodHandle -> {
                    val referenceKind = input.readByte()
                    val referenceIndex = input.readShort()
                    MethodHandleInfo(referenceKind, referenceIndex)
                }

                TagMethodType -> {
                    val descriptorIndex = input.readShort()
                    MethodTypeInfo(descriptorIndex)
                }

                TagInvokeDynamic -> {
                    val bootstrapMethodAttrIndex = input.readShort()
                    val nameAndTypeIndex = input.readShort()
                    InvokeDynamicInfo(bootstrapMethodAttrIndex, nameAndTypeIndex)
                }

                TagModule -> {
                    val nameIndex = input.readShort()
                    ModuleInfo(nameIndex)
                }

                TagPackage -> {
                    val nameIndex = input.readShort()
                    PackageInfo(nameIndex)
                }

                else -> throw RuntimeException("Don't know how to handle constant pool tag $tag")
            }
            return info
        }
    }
}
