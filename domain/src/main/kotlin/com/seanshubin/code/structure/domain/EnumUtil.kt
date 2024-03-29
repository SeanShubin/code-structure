package com.seanshubin.code.structure.domain

object EnumUtil {
    inline fun <reified T : Enum<T>> defaultString() =
        enumValues<T>().joinToString(", ", "choose one of: ") { it.name.lowercase() }

    inline fun <reified T : Enum<T>> Any?.coerceToEnum(): T =
        when (this) {
            is String ->
                enumValues<T>().find { it.name == this.uppercase() }
                    ?: throw RuntimeException("Unexpected ${T::class.simpleName} '$this', ${defaultString<T>()}")

            is T -> this
            else -> throw RuntimeException("Unexpected ${T::class.simpleName} '$this', ${defaultString<T>()}")
        }
}
