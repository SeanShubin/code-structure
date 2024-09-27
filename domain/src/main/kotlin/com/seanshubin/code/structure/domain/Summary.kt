package com.seanshubin.code.structure.domain

data class Summary(val errors:List<ErrorSummaryItem>, val errorLimit:Int){
    val errorCount:Int = errors.filter { it.isPartOfTotal }.sumOf { it.count }
}
