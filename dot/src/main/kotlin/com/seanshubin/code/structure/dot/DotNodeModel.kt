package com.seanshubin.code.structure.dot

data class DotNodeModel(val id:String, val attributes:List<Pair<String, String>>) {
    fun toDotLine():String {
        val quotedId = id.quote()
        return if(attributes.isEmpty()){
            quotedId
        } else {
            val attributesString = attributes.map{it.toDotFormat()}.joinToString(" ", "[", "]")
            "$quotedId $attributesString"
        }
    }
    private fun Pair<String, String>.toDotFormat():String {
        val quotedSecond = second.quote()
        return "$first=$quotedSecond"
    }
    private fun String.quote(): String = "\"$this\""
}
