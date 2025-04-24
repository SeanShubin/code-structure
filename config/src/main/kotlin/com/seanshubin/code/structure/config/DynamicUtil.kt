package com.seanshubin.code.structure.config

object DynamicUtil {
    fun get(o: Any?, path: List<Any?>): Any? = Dynamic(o).get(path).o

    fun set(o: Any?, path: List<Any?>, value: Any?): Any? = Dynamic(o).set(path, value).o

    fun setWithArrays(o: Any?, path: List<Any?>, value: Any?, arrayDefaults:List<Any?>): Any? = Dynamic(o).setWithArrays(path, value, arrayDefaults).o

    fun exists(o: Any?, path: List<Any?>): Boolean = Dynamic(o).exists(path)

    fun flattenListAt(o: Any?, path: List<Any?>): List<Any?> = Dynamic(o).flattenListAt(path).map { it.o }

    fun flattenListWithIndex(o: Any?, path: List<Any?>, valueKey: Any?, indexKey: Any?): List<Any?> =
        Dynamic(o).flattenListWithIndex(path, valueKey, indexKey).map { it.o }

    fun flattenMap(o: Any?, combineKey: (Any?, Any?) -> Any?): Any? = Dynamic(o).flattenMap(combineKey).o

    fun update(o: Any?, path: List<Any?>, default: Any?, operation: (Any?) -> Any?): Any? =
        Dynamic(o).update(path, default, operation).o

    fun typeHistogram(o: Any?): Map<String, Map<String, Int>> = Dynamic(o).typeHistogram()

    fun accumulateTypeHistogram(typeHistogram: Map<String, Map<String, Int>>, o: Any?): Map<String, Map<String, Int>> {
        val newTypeHistogram = Dynamic(o).typeHistogram()
        return addOuterHistograms(typeHistogram, newTypeHistogram)
    }

    private fun addOuterHistograms(
        a: Map<String, Map<String, Int>>,
        b: Map<String, Map<String, Int>>
    ): Map<String, Map<String, Int>> {
        val allKeys = (a.keys + b.keys).distinct()
        return allKeys.associateWith { key ->
            val aHistogram = a[key] ?: emptyMap()
            val bHistogram = b[key] ?: emptyMap()
            addInnerHistograms(aHistogram, bHistogram)
        }
    }

    private fun addInnerHistograms(a: Map<String, Int>, b: Map<String, Int>): Map<String, Int> {
        val allKeys = (a.keys + b.keys).distinct()
        return allKeys.associateWith { key ->
            val aQuantity = a[key] ?: 0
            val bQuantity = b[key] ?: 0
            aQuantity + bQuantity
        }
    }
}
