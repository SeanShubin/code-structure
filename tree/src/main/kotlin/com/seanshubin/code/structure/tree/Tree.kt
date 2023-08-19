package com.seanshubin.code.structure.tree

sealed interface Tree<KeyType, ValueType> {
    fun setValue(path: List<KeyType>, value: ValueType): Tree<KeyType, ValueType>
    fun getValue(path: List<KeyType>): ValueType?
    fun toLines(
        keyOrder: Comparator<KeyType>,
        keyToString: (KeyType) -> String,
        valueToString: (ValueType) -> String
    ): List<String>

    fun pathValues(path: List<KeyType>): List<Pair<List<KeyType>, ValueType>>

    data class Branch<KeyType, ValueType>(val parts: Map<KeyType, Tree<KeyType, ValueType>>) :
        Tree<KeyType, ValueType> {
        override fun setValue(path: List<KeyType>, value: ValueType): Tree<KeyType, ValueType> {
            if (path.isEmpty()) return Leaf(value)
            val key = path[0]
            val remainingPath = path.drop(1)
            val innerValue = when (val existingInnerValue = parts[key]) {
                is Branch -> existingInnerValue
                else -> empty()
            }
            val newInnerValue = innerValue.setValue(remainingPath, value)
            val newValue = Branch(parts + (key to newInnerValue))
            return newValue
        }

        override fun getValue(path: List<KeyType>): ValueType? {
            if (path.isEmpty()) return null
            val key = path[0]
            val remainingPath = path.drop(1)
            val innerValue = when (val existingInnerValue = parts[key]) {
                is Branch -> existingInnerValue
                is Leaf -> return existingInnerValue.value
                null -> return null
            }
            val finalValue = innerValue.getValue(remainingPath)
            return finalValue
        }

        override fun toLines(
            keyOrder: Comparator<KeyType>,
            keyToString: (KeyType) -> String,
            valueToString: (ValueType) -> String
        ): List<String> {
            val keys = parts.keys.toList().sortedWith(keyOrder)
            val lines = keys.flatMap { key ->
                val value = parts.getValue(key)
                val thisLine = keyToString(key)
                val subLines = value.toLines(keyOrder, keyToString, valueToString).map { "  $it" }
                listOf(thisLine) + subLines
            }
            return lines
        }

        override fun pathValues(path: List<KeyType>): List<Pair<List<KeyType>, ValueType>> {
            return parts.flatMap { (key, value) ->
                val newPath = path + key
                value.pathValues(newPath)
            }
        }
    }

    data class Leaf<KeyType, ValueType>(val value: ValueType) : Tree<KeyType, ValueType> {
        override fun setValue(path: List<KeyType>, value: ValueType): Tree<KeyType, ValueType> {
            return empty<KeyType, ValueType>().setValue(path, value)
        }

        override fun getValue(path: List<KeyType>): ValueType? {
            return if (path.isEmpty()) this.value
            else null
        }

        override fun toLines(
            keyOrder: Comparator<KeyType>,
            keyToString: (KeyType) -> String,
            valueToString: (ValueType) -> String
        ): List<String> = listOf(valueToString(value))

        override fun pathValues(path: List<KeyType>): List<Pair<List<KeyType>, ValueType>> {
            return listOf(path to value)
        }

    }

    companion object {
        fun <KeyType, ValueType> empty(): Branch<KeyType, ValueType> = Branch(emptyMap())
    }
}
