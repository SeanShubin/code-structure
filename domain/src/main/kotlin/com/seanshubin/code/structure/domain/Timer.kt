package com.seanshubin.code.structure.domain

interface Timer {
    fun <T> monitor(caption:String, f:()->T):T
}
