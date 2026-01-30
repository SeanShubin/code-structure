package com.seanshubin.code.structure.config

data class Dynamic(val o: Any?) {
    fun get(path: List<Any?>): Dynamic {
        if (path.isEmpty()) return this
        val key = path.first()
        return if (o is Map<*, *>) {
            val subPath = path.drop(1)
            val subTree = Dynamic(o[key])
            subTree.get(subPath)
        } else if (o is List<*> && key is Int) {
            val subPath = path.drop(1)
            val subTree = Dynamic(o[key])
            subTree.get(subPath)
        } else {
            throw RuntimeException("Path not found ${path.joinToString(".")}")
        }
    }

    fun set(path: List<Any?>, value: Any?): Dynamic {
        if (path.isEmpty()) return Dynamic(value)
        return if (o is Map<*, *>) {
            val key = path.first()
            val oldSubTree = Dynamic(o[key])
            val subPath = path.drop(1)
            val newSubTree = oldSubTree.set(subPath, value)
            val entry = key to newSubTree.o
            Dynamic(o + entry)
        } else {
            Dynamic(emptyMap<Any?, Any?>()).set(path, value)
        }
    }

    fun setWithArrays(path: List<Any?>, value: Any?, arrayDefaults: List<Any?>): Dynamic {
        if (path.isEmpty()) return Dynamic(value)
        val key = path.first()
        return if (key is Int) {
            val defaultValue = arrayDefaults.first()
            if (o is List<*>) {
                val oldSubTree = Dynamic(o.getOrNull(key))
                val newSubTree = oldSubTree.setWithArrays(path.drop(1), value, arrayDefaults.drop(1))
                val newValue = updateArray(o, key, defaultValue, newSubTree.o)
                return Dynamic(newValue)
            } else {
                Dynamic(emptyList<Any?>()).setWithArrays(path, value, arrayDefaults)
            }
        } else if (o is Map<*, *>) {
            val oldSubTree = Dynamic(o[key])
            val subPath = path.drop(1)
            val newSubTree = oldSubTree.setWithArrays(subPath, value, arrayDefaults)
            val entry = key to newSubTree.o
            Dynamic(o + entry)
        } else {
            Dynamic(emptyMap<Any?, Any?>()).setWithArrays(path, value, arrayDefaults)
        }
    }

    private fun updateArray(o: Any?, index: Int, defaultValue: Any?, value: Any?): List<Any?> {
        if (o is List<*>) {
            if (o.size < index) {
                return o + (o.size until index).map { defaultValue } + value
            } else {
                val before = o.take(index)
                val after = o.drop(index + 1)
                return before + listOf(value) + after
            }
        } else {
            return (0 until index).map { defaultValue } + value
        }
    }

    fun exists(path: List<Any?>): Boolean {
        if (path.isEmpty()) return true
        return if (o is Map<*, *>) {
            val key = path.first()
            if (o.containsKey(key)) {
                val subPath = path.drop(1)
                val subTree = Dynamic(o[key])
                subTree.exists(subPath)
            } else {
                false
            }
        } else {
            false
        }
    }

    fun flattenListAt(path: List<Any?>): List<Dynamic> {
        val theList = get(path).o as List<Any?>
        return theList.map { element ->
            set(path, element)
        }
    }

    fun flattenListWithIndex(path: List<Any?>, valueKey: Any?, indexKey: Any?): List<Dynamic> {
        val theList = get(path).o as List<Any?>
        return theList.mapIndexed { index, element ->
            set(path + valueKey, element).set(path + indexKey, index)
        }
    }

    fun flattenMap(combineKeys: (Any?, Any?) -> Any?): Dynamic {
        return when (o) {
            is Map<*, *> -> {
                Dynamic(o.flatMap { (key, value) ->
                    val flattenedInner = Dynamic(value).flattenMap(combineKeys).o
                    if (flattenedInner is Map<*, *>) {
                        flattenedInner.map { (innerKey, innerValue) ->
                            val newKey = combineKeys(key, innerKey)
                            newKey to innerValue
                        }
                    } else {
                        listOf(key to flattenedInner)
                    }
                }.toMap())
            }

            is List<*> -> {
                val asMap = o.mapIndexed { index, value ->
                    index to value
                }.toMap()
                Dynamic(asMap).flattenMap(combineKeys)
            }

            else -> this
        }
    }

    fun update(path: List<Any?>, default: Any?, operation: (Any?) -> Any?): Dynamic {
        val oldValue = if (exists(path)) get(path).o else default
        val newValue = operation(oldValue)
        return set(path, newValue)
    }

    fun typeHistogram(): Map<String, Map<String, Int>> {
        val combineKeys = { a: Any?, b: Any? -> "$a.$b" }
        val flat = flattenMap(combineKeys).o
        if (flat !is Map<*, *>) return listOf(histogramEntry("", this)).toMap()
        val result = flat.map { (key, value) ->
            histogramEntry(key, value)
        }.toMap()
        return result
    }

    private fun histogramEntry(key: Any?, value: Any?): Pair<String, Map<String, Int>> {
        val typeName = value?.javaClass?.simpleName ?: "Nothing?"
        return "$key" to mapOf(typeName to 1)
    }
}
