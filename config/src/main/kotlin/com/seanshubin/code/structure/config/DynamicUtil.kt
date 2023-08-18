package com.seanshubin.code.structure.config

object DynamicUtil {
    fun setValueAtPath(path:List<Any?>, theObject:Any?, targetValue:Any?):Any? {
        if(path.isEmpty()) return targetValue
        val key = path[0]
        val remainingPath = path.drop(1)
        val map = if(theObject is Map<*,*>){
            theObject
        } else {
            mapOf<Any?, Any?>()
        }
        val innerValue = map[key]
        val newInnerValue = setValueAtPath(remainingPath, innerValue, targetValue)
        val newObject = map + (key to newInnerValue)
        return newObject
    }

    fun getValueAtPath(path:List<Any?>, theObject:Any?):Any? {
        if(path.isEmpty()) return theObject
        val map = theObject as Map<*,*>
        val key = path[0]
        val remainingPath = path.drop(1)
        val innerValue = map[key]
        val finalValue = getValueAtPath(remainingPath, innerValue)
        return finalValue
    }

    fun pathExists(path:List<Any?>, theObject:Any?):Boolean {
        if(path.isEmpty()) return true
        if(theObject !is Map<*,*>) return false
        val key = path[0]
        if(!theObject.containsKey(key)) return false
        val remainingPath = path.drop(1)
        val innerValue = theObject[key]
        return pathExists(remainingPath, innerValue)
    }
}
