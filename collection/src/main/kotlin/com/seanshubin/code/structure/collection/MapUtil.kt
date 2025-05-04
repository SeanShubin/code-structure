package com.seanshubin.code.structure.collection

object MapUtil {
    fun <T, U> mapAddToList(map:Map<T, List<U>>, pair: Pair<T, U>):Map<T, List<U>>{
        val (key, value) = pair
        val oldList = map[key] ?: emptyList()
        val newList = oldList + value
        return map + (key to newList)
    }

    fun <T,U> Map<T, List<U>>.addToList(pair: Pair<T, U>):Map<T, List<U>> = mapAddToList<T,U>(this, pair)
}
