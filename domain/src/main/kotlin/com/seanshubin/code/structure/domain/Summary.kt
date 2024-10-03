package com.seanshubin.code.structure.domain

data class Summary(val errors:Map<ErrorType, ErrorSummaryItem>, val errorLimit:Int){
    val errorCount:Int = errors.values.filter { it.isPartOfTotal }.sumOf { it.count }
    val isOverLimit:Boolean = errorCount > errorLimit
}
