package com.seanshubin.code.structure.config

interface Configuration {
    fun load(path:List<String>, default:Any?): Any?
}
