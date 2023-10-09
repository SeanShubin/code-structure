package com.seanshubin.code.structure.jvmformat

interface ConstantPoolInfo {
    companion object {
        data class ClassInfo(val nameIndex: Short) : ConstantPoolInfo

        data class FieldRefInfo(val classIndex: Short, val nameAndTypeIndex: Short) : ConstantPoolInfo

        data class MethodRefInfo(val classIndex: Short, val nameAndTypeIndex: Short) : ConstantPoolInfo

        data class InterfaceMethodRefInfo(val classIndex: Short, val nameAndTypeIndex: Short) : ConstantPoolInfo

        data class StringInfo(val stringIndex: Short) : ConstantPoolInfo

        data class IntegerInfo(val value: Int) : ConstantPoolInfo

        data class FloatInfo(val value: Float) : ConstantPoolInfo

        data class LongInfo(val value: Long) : ConstantPoolInfo

        data class DoubleInfo(val value: Double) : ConstantPoolInfo

        data class NameAndTypeInfo(val nameIndex: Short, val descriptorIndex: Short) : ConstantPoolInfo

        data class Utf8Info(val value: String) : ConstantPoolInfo
        data class MethodHandleInfo(val referenceKind: Byte, val referenceIndex: Short) : ConstantPoolInfo

        data class MethodTypeInfo(val descriptorIndex: Short) : ConstantPoolInfo

        data class InvokeDynamicInfo(val bootstrapMethodAttrIndex: Short, val nameAndTypeIndex: Short) :
            ConstantPoolInfo

        data class ModuleInfo(val nameIndex: Short) : ConstantPoolInfo
        data class PackageInfo(val nameIndex: Short) : ConstantPoolInfo

        object Unusable : ConstantPoolInfo
    }
}
